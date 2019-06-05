package com.imooc.miaosha.redis;

//使用模板模式来定义
//用来区分redis中的key
//使用前缀 + keyName区分
public interface KeyPrefix {

    //有效期
    public int expireSeconds();

    //前缀
    public String getPrefix();
}
