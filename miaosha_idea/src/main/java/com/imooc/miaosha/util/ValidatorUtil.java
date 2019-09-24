package com.imooc.miaosha.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断格式是否合法工具类
 */
public class ValidatorUtil {

    //判断手机号码正则表达式,1后面跟着10个数字
    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    /**
     * 判断手机号是否合法
     * @param mobileNum 待验证的手机号码
     * @return 验证结果
     */
    public static boolean isMobile(String mobileNum){
        if (StringUtils.isEmpty(mobileNum)){
            return false;
        }
        Matcher m = MOBILE_PATTERN.matcher(mobileNum);
        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("1231234124"));
    }
}
