package com.imooc.miaosha.redis;

//每个模块一个类
public class OrderKey extends BasePrefix {
    public OrderKey( String prefix) {
        super(prefix);
    }
    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
