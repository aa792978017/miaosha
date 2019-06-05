package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void recevieMiaosha(String message){
        //真正开始处理订单
        log.info("receive msg :" + message);
        MiaoshaMessage mm =  RedisService.stringToBean(message,MiaoshaMessage.class);
        MiaoshaUser user = mm.getMiaoshaUser();
        long goodsId = mm.getGoodsId();

        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0){
            return;
        }
         //判断是否已经秒杀过了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){ //不能重复秒杀
            return ;
        }

        //减库存,下订单,写入秒杀订单
        miaoshaService.miaosha(user, goods);


    }





    @RabbitListener(queues = MQConfig.QUEUE)
    public void recevie(String message){
        log.info("receive msg :" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void recevieTopic1(String message){
        log.info("topic queue1 message :" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void recevieTopic2(String message){
        log.info("topic queue2 message :" + message);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void recevieHeaders(byte[] message){
        log.info("header queue message :" + new String(message));
    }
}
