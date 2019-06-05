package com.imooc.miaosha.redis;



public class UserKey extends BasePrefix {
    //永不过期
    public UserKey(String prefix) {
        super(prefix);
    }
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
