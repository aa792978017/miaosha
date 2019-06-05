package com.imooc.miaosha.validator;
import com.imooc.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//校验器标准,通过注解来全局管理
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> { //注解,和注解参数字段类型

    private boolean required = false;

    //初始化方法
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        //把注解的参数传过来
       required =  constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
       //默认需要判断
        if (required){
           return ValidatorUtil.isMobile(value);
       }else{
            //不需要判断的时候,如果为空返回真
           if (StringUtils.isEmpty(value)){
               return true;
           }else{
               //如果不为空,就判断一下
               return ValidatorUtil.isMobile(value);
           }
       }

    }
}
