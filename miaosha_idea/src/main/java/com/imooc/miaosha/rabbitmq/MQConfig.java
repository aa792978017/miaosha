package com.imooc.miaosha.rabbitmq;

import javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanProperty;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final  String MIAOSHA_QUEUE = "miaosha.queue";

    public static final  String QUEUE = "queue";
    public static final  String TOPIC_QUEUE1 = "topic.queue1";
    public static final  String TOPIC_QUEUE2 = "topic.queue2";
    public static final  String HEADERS_QUEUE = "headers.queue";
    public static final  String TOPIC_EXCHANGE = "topicExchange";
    public static final  String FANOUT_EXCHANGE = "fanoutExchange";
    public static final  String HEADERS_EXCHANGE = "headersExchange";



    /**
     * Direct模式,一共有四种交换机模式这一种最简单的
     */
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOSHA_QUEUE, true);
    }

    /**
     * Direct模式,一共有四种交换机模式这一种最简单的
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE, true);
    }

    /**--------------------------------------------------------------------------------------------
     * Topic模式
     * 创建两个Topic队列,可以带通配符,可以发给多个QUEUE
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }

    /**
     * 创建一个Topic交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    //交换机和Queue做一个绑定,设置路由key
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    //Fanout模式(广播模式)--------------------------------------------------------------------------
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    //交换机和Queue做一个绑定,进行广播,所有绑定了的Queue都能收到
    @Bean
    public Binding FanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding FanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    //Header模式(满足kv才能接受)--------------------------------------------------------------------------
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headersQueue(){
        return new Queue(HEADERS_QUEUE,true);
    }

    @Bean
    public Binding headersBinding1(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();  //必须满足map所有keyvalue,才能放东西去Queue
    }




}
