package com.imooc.miaosha.util;

import java.util.UUID;

public class UUIDUtil {
    public static String uuid(){
        //yuan sheng de you -
        return UUID.randomUUID().toString().replace("-","");

    }
}
