package com.imooc.miaosha.config;

import com.imooc.miaosha.Context.UserContext;
import com.imooc.miaosha.domain.MiaoshaUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 全局使用,根据token去获取用户信息
 * 先从redis获取,没有就从数据库查询,放到redis里面
 */
@Component
public class UserArgumentResolver  implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz== MiaoshaUser.class;  //如果为真,执行下面的方法
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
//        HttpServletResponse response  = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

        return UserContext.getUser();
    }

}
