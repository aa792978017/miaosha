package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import jdk.nashorn.internal.objects.Global;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 处理全局异常,所有地方出现异常后都会都通这里返回页面
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Exception.class)  //拦截哪些类型的异常
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        if (e instanceof GlobalException){ //处理全局异常
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }else if (e instanceof BindException){ //处理绑定异常,
            //todo  不理解这绑定异常是啥 标志在试图绑定套接字和本地端口时出现错误。一般情况下，在端口已被使用或者请求本地端口不能被分配时抛出
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }
}
