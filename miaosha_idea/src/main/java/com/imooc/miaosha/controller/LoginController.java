package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.UserService;

import com.imooc.miaosha.util.ValidatorUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 登录接口
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private RedisService redisService;

    @Autowired
    private MiaoShaUserService miaoShaUserService;

    /**
     * 返回登录页面
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录
     * @param response
     * @param loginVo  使用校验器进行登录账号密码格式检验
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Validated LoginVo loginVo) { //进行参数校验
        //输出登录信息
        log.info(loginVo.toString());
        //登录
        String token = miaoShaUserService.login(response,loginVo);
        return Result.success(token);

    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById , ""+1, User.class);

        return Result.success(user);
    }

}

