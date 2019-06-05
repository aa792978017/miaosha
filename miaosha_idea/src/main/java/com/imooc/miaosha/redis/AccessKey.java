package com.imooc.miaosha.redis;

//每个模块一个类
public class AccessKey extends BasePrefix {
    public AccessKey(int expierSeconde, String prefix) {
        super(expierSeconde,prefix);
    }

    public static AccessKey assess = new AccessKey(2,"access");  //商品列表的缓存页面
    public static AccessKey withExpire(int expierSeconde){
        return new AccessKey(expierSeconde,"access");
    }
}
