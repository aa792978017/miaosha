package com.imooc.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//redis连接池工厂Bean
@Component
public class RedisPoolFactory {

    @Autowired
    private RedisConfig redisConfig;

    //返回一个jedisPool连接池Bean
    @Bean
    public JedisPool jedisPoolFactory(){

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        return new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort()
                ,redisConfig.getTimeout()*1000, redisConfig.getPassword(), 0);

    }
}
