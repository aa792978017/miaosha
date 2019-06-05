package com.imooc.miaosha.redis;

//每个模块一个类
public class GoodsKey extends BasePrefix {
    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");  //商品列表的缓存页面
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd"); //商品详情页面
    public static GoodsKey MiaoshaGoodsStock = new GoodsKey(0,"gs"); //商品详情页面


}
