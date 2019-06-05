package com.imooc.miaosha.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int seconds();  //失效时间
    int maxCount(); //最大次数
    boolean needLogin() default true; //默认是否需要登录

}
