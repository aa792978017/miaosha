package com.imooc.miaosha.controller;

import com.imooc.miaosha.config.ConfigKV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试Nginx负载均衡
 */
@RestController
@RequestMapping("/nginx")
public class NginxController {

    //正常请求
    private static final int NORMAL_FLAG = 1;

    //异常请求
    private static final int EXCEPTION_FLAG = 0;

    @Autowired
    private ConfigKV configKV;

    /**
     * 返回端口名称
     * @return 端口号
     */
    @RequestMapping(value = "/sayPoint",method = RequestMethod.GET)
    public String sayPoint() {
        System.out.println(configKV.getPort());
        return configKV.getPort();
    }

    /**
     * 测试HTTP状态码,
     * 测试路径传参
     * 测试多url对应
     * @param response 返回体
     * @param flag 判断请求类型
     * @return 相应结果
     */
    @RequestMapping(value = {"/test","/http/{flag}/test"})
    public String testHttpStatusCode(HttpServletResponse response, @PathVariable int flag){
        if (NORMAL_FLAG == flag) {
            //对HTTP的所有设置,都可以通过这个HttpServletResponse进行配置
            response.setStatus(200);
            return "success";
        }
        if (EXCEPTION_FLAG == flag) {
            response.setStatus(404);
            return "exception";
        }
        ReentrantLock
        return "error";
    }
}
