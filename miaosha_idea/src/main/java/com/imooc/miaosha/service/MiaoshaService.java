package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaService {

	@Autowired
	private RedisService redisService;

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private OrderService orderService; //不要注入别的dao,这样分层容易混乱,

	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减少库存,下订单,写入秒杀订单
		if(goodsService.reduceStock(goods)<=0){
			setGoodsOver(goods.getId());
			return null;
		}
		//order_info miaosha_order
		return orderService.createOrder(user,goods);

	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if (order != null){ //秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if (isOver){
				return -1;
			}else {
				return 0;
			}
		}
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exit(MiaoshaKey.isGoodsOver,""+goodsId);
	}

	private void setGoodsOver(long goodsId){
		redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
	}

	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}

    public boolean checkPath(String path, MiaoshaUser user, long goodsId) {
		if (user == null || path == null){
			return false;
		}
		String pathOld =redisService.get(MiaoshaKey.getMiaoshaPath,
				""+user.getId()+"_"+goodsId,String.class);
		return path.equals(pathOld);
    }

	public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
		//根据userid和goodsid生成字符串
		String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
		redisService.set(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId, str);
		return str;
	}

	public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
		if (user == null || goodsId <= 0){
			return null;
		}
		int width = 80;
		int height = 32;
		//创建图片
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics graphic = image.getGraphics();
		//设置背景颜色
		graphic.setColor(new Color(0xDCDCDC));
		graphic.fillRect(0,0,width,height);
		//画边框
		graphic.setColor(Color.BLACK);
		graphic.drawRect(0,0,width-1,height-1);
		//创建随机数字
		Random rdm = new Random();
		//画干扰点
		for (int i = 0; i < 1000; i++){
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			graphic.drawOval(x,y,0,0);
		}
		//生成随机表达式字符串,并画在图片上
		String verifyCode = generateVerifyCode(rdm);
		graphic.setColor(new Color(0,100,0));
		graphic.setFont(new Font("Candara",Font.BOLD,20));
		graphic.drawString(verifyCode,8,24);
		graphic.dispose();
		int rnd = calc(verifyCode);
		redisService.set(MiaoshaKey.getMiaoshaVerifyCode,user.getId()+"_"+goodsId,rnd);
		return image;

	}



	private static int calc(String verifyCode) {
		try{
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine se = manager.getEngineByName("JavaScript");
			return (Integer)se.eval(verifyCode);

		}catch (Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	private static char[] ops = new char[]{'+', '-', '*'};

	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
		int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = "" + num1 + op1 + num2 + op2 + num3;
		return exp;

	}

	public boolean checkVerifyCode(int verifyCode, long goodsId, MiaoshaUser user) {
		if (goodsId <= 0 || user == null){
			return false;
		}
		Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode,user.getId()+"_"+goodsId,Integer.class);
		if (codeOld == null || codeOld - verifyCode != 0){
			return false;
		}else{
			redisService.del(MiaoshaKey.getMiaoshaVerifyCode,user.getId()+"_"+goodsId);
		}
		return true;
	}
}
