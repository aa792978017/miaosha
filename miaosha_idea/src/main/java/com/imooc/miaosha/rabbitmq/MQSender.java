package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send miaosha msg:" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }






    public void send(Object message){
        String msg = RedisService.beanToString(message);  //对象变为String
        log.info("send msg:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);  //对象变为String
        log.info("send msg:" + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");

    }

    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);  //对象变为String
        log.info("send msg:" + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+"1");
    }

    public void sendHeaders(String message){
        String msg = RedisService.beanToString(message);  //对象变为String
        log.info("send msg:" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header", "value1");
        properties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"", obj);
    }


}
