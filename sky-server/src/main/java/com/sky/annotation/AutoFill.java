package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，代表该方法需要填充公共字段
 */
@Target(ElementType.METHOD)//加在方法上
@Retention(RetentionPolicy.RUNTIME)//指定注解的保留策略,注解在编译后的字节码文件中保留，并且可以在运行时通过反射机制访问
public @interface AutoFill {
    //指定操作类型，insert与update
    OperationType value();//代表该注解的value可以设置为OperationType类型的值即INSERT与UPDATE
}
