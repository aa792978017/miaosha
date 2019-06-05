package com.imooc.miaosha.controller;

import com.imooc.miaosha.access.AccessLimit;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.*;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.*;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.ibatis.executor.ReuseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> isOverMap = new HashMap<>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //把秒杀商品存到redis,预加载
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos == null){
            return;
        }
        //把所有秒杀商品的库存,存到redis
        for (GoodsVo goodsVo : goodsVos){
            redisService.set(GoodsKey.MiaoshaGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            isOverMap.put(goodsVo.getId(),false);
        }

    }


    /**
     * orderId :成功
     * -1 失败
     * 0  排队
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/result", method= RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) { //用户没有登录的话就跳转到登录页面
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    /**
     * QPS:1816
     * 5000 * 10
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/{path}/do_miaosha", method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser user,
                                  @RequestParam("goodsId")long goodsId,
                                @PathVariable("path") String path){
        model.addAttribute("user",user);
        if (user == null){ //用户没有登录的话就跳转到登录页面
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = miaoshaService.checkPath(path,user,goodsId);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存判断商品是否已经售空
        if (isOverMap.get(goodsId)){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //预减少库存
        long stock = redisService.decr(GoodsKey.MiaoshaGoodsStock,""+goodsId);
        if (stock < 0){ //从redis判断库存,如果库存已经没有了,直接返回
            isOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀过了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){ //不能重复秒杀
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setMiaoshaUser(user);
        mqSender.sendMiaoshaMessage(mm);
        return Result.success(0);//排队中

    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=5, maxCount = 5,needLogin = true)
    @RequestMapping(value="/path", method= RequestMethod.GET)
    @ResponseBody
    public Result<String> miaoshaPath(MiaoshaUser user,
                                      HttpServletRequest request,
                                      @RequestParam("goodsId")long goodsId,
                                      @RequestParam(value = "verifyCode", defaultValue = "0")int verifyCode){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //检查验证码是否正确
        boolean check = miaoshaService.checkVerifyCode(verifyCode,goodsId,user);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //返回秒杀路径
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/verifyCode", method= RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response,MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miaoshaService.createVerifyCode(user,goodsId);
        try{
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG", out);
            out.flush();
            out.flush();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAO_SHA_FAIL);
        }

    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model){
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList){
            goods.setStockCount(10);
            redisService.set(GoodsKey.MiaoshaGoodsStock,""+goods.getId(),10);
            isOverMap.put(goods.getId(),false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);

    }

//    /**
//     * QPS:1816
//     * 5000 * 10
//     * 秒杀接口优化前(第六章前)
//     * QPS: 3569
//     * @param model
//     * @param user
//     * @param goodsId
//     * @return
//     */
//    @RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
//    @ResponseBody
//    public Result<OrderInfo> list(Model model, MiaoshaUser user,
//                       @RequestParam("goodsId")long goodsId){
//        model.addAttribute("user",user);
//        if (user == null){ //用户没有登录的话就跳转到登录页面
//            return Result.error(CodeMsg.SESSION_ERROR);
//        }
//        //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getStockCount();
//        if (stock <= 0){
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        //判断是否已经秒杀过了
//        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
//        if(miaoshaOrder != null){ //不能重复秒杀
//            return Result.error(CodeMsg.REPEAT_MIAOSHA);
//        }
//        //减库存,下订单,写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
////        model.addAttribute("orderInfo",orderInfo);
////        model.addAttribute("goods", goods);
//        return Result.success(orderInfo);
//
//    }



}

