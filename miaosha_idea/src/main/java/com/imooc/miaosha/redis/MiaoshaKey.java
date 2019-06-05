package com.imooc.miaosha.redis;

//每个模块一个类
public class MiaoshaKey extends BasePrefix {
    public MiaoshaKey(int expierSeconde, String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");  //商品列表的缓存页面
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp"); //获取秒杀商品地址
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300,"vc");  //商品列表的缓存页面

}
