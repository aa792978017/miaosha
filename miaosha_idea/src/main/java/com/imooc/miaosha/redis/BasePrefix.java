package com.imooc.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    //默认不过期
    public BasePrefix(String prefix){
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }


    @Override
    public int expireSeconds() {
        //默认0为永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
