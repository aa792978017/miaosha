package com.imooc.miaosha.interceptor;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.Context.UserContext;
import com.imooc.miaosha.access.AccessLimit;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.MiaoshaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MiaoShaUserService miaoShaUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            //获取User信息,放入线程中
            MiaoshaUser user = getMiaoshaUser(request,response);
            UserContext.setUser(user);
            //获取方法
            HandlerMethod hm = (HandlerMethod)handler;
            //获取注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //判断是否存在访问而限制注解
            if (accessLimit == null){
                return true;
            }
            //获取注解里面的参数
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin){
                if (user == null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }else{
                //do nothing
            }

            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak,"" + key, Integer.class);
            if (count == null){
                redisService.set(ak, key,1);
            }else if (count < maxCount){
                redisService.incr(ak,key);
            }else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }

        }

        return super.preHandle(request, response, handler);
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws IOException {
        OutputStream out = response.getOutputStream();
        response.setContentType("application/json;charset=UTF-8");
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getMiaoshaUser(HttpServletRequest request, HttpServletResponse response){
        String paramToken = request.getParameter(MiaoShaUserService.COOKI_NAME_TOKEN);  //从request参数里面获取token
        String cookieToken = getCookieValue(request, MiaoShaUserService.COOKI_NAME_TOKEN); //从cookie里面获取token
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){   //如果两个地方都没有,则是第一次登录
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken; //优先使用 paramToken来获取token
        MiaoshaUser user = miaoShaUserService.getByToken(response, token); //根据token来获取用户信息
        return user;
    }


    /**
     * 遍历cookie获取用户token
     * @param request
     * @param cookiNameToken
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookiNameToken) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies){
            if (cookie.getName().equals(cookiNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
