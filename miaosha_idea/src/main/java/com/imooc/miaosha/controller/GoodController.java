package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.UserService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.context.ApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodController {

    private static Logger log = LoggerFactory.getLogger(GoodController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;



    @Autowired
    private RedisService redisService;

    @Autowired
    private MiaoShaUserService miaoShaUserService;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver; //注入thymeleaf视图解析器

    @Autowired
    private ApplicationContext applicationContext;   //注入spring的上下文



    /**
     * QPS:1433
     * 5000*10
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list",produces = "text/html")  //从redis返回缓存html页面
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user){
        model.addAttribute("user",user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "",String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染页面
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;

    }

    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")  //restful风格
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response,
                         Model model,MiaoshaUser user,
                         @PathVariable("goodsId")long goodsId) {  //数据库里面的id很少自增,因为很容易被遍历出来
        model.addAttribute("user", user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }


        //查询秒杀商品
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        //秒杀开始时间	model.addAttribute("goods", goods);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;  //秒杀商品状态
        int remainSeconds = 0;  //秒杀商品开始倒计时

        if (now < startAt){ //秒杀还没开始
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        }else if(now > endAt){ //秒杀已经结束了
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else { //秒杀正在进行
            miaoshaStatus = 1;
            remainSeconds = 0;
        }


        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染页面
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }
        return html;
    }

    @RequestMapping(value="/detail/{goodsId}")  //restful风格,页面静态化
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, MiaoshaUser user,
                                        @PathVariable("goodsId")long goodsId) {  //数据库里面的id很少自增,因为很容易被遍历出来
        model.addAttribute("user", user);
//        //取缓存
//        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
//        if (!StringUtils.isEmpty(html)){
//            return html;
//        }


        //查询秒杀商品
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        //秒杀开始时间	model.addAttribute("goods", goods);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;  //秒杀商品状态
        int remainSeconds = 0;  //秒杀商品开始倒计时

        if (now < startAt){ //秒杀还没开始
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        }else if(now > endAt){ //秒杀已经结束了
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else { //秒杀正在进行
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
//        model.addAttribute("miaoshaStatus", miaoshaStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        SpringWebContext ctx = new SpringWebContext(request, response,
//                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
//        //手动渲染页面
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
//        if (!StringUtils.isEmpty(html)){
//            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
//        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setUser(user);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setGoods(goods);
        return Result.success(goodsDetailVo);
    }



}

