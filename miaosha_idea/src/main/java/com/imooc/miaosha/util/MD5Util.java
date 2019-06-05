package com.imooc.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }
    //盐
    private static final String salt = "1a2b3c4d";

    /**
     * 第一次加密,获得表单密码
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) +
                inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密,获得数据库密码
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) +
                formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    //从明文密码两次加密,或得数据库密码
    public static String inputPassToDbPass(String input, String saltDb){
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass, saltDb);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456")); //d3b1294a61a07da9b49b6e22b2cbd7f9
    }


}
