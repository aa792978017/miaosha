package com.imooc.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 * 用来做MD5加密明文密码
 */
public class MD5Util {

    //盐
    private static final String salt = "1a2b3c4d";

    /**
     * DM5
     * @param key 要做MD5的字符串
     * @return 返回MD5加密后的字符串
     */
    public static String md5(String key) {
        return DigestUtils.md5Hex(key);
    }

    /**
     * 第一次加密,获得表单密码
     * 意义:
     * MD5:HTTP协议在网络中是明文传输,这样可以防止明文密码在网络中传输;
     * salt:防止通过彩虹表反查出密码;
     * @param inputPass 明文密码
     * @return  返回第一次加密后的密码
     */
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) +
                inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密,获得数据库密码
     * 意义:防止数据库被盗后,通过彩虹表反查出第一次加密后的密码
     * @param formPass 前端表单提交上来的密码
     * @param salt 随机生成的salt,会存到数据库中
     * @return 返回第二次加密后的密码
     */
    public static String formPassToDBPass(String formPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) +
                formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 将明文密码加密为要存到数据库的密码
     * @param inputPass 明文密码
     * @param saltDb 随机盐,会存储到数据库中
     * @return 存到数据库的密码
     */
    public static String inputPassToDbPass(String inputPass, String saltDb){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDb);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456")); //d3b1294a61a07da9b49b6e22b2cbd7f9
    }


}
