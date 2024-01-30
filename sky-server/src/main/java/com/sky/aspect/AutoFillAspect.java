package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，实现对创建人创建时间修改人修改时间等公共字段的填充
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    /**
     * 拦截mapper包下加了AutoFill注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在mapper代理对象执行增改操作之前就进行公共字段填充
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        //通过jointPoint获取拦截到的方法的信息
        log.info("进行公共字段填充");
        //获取AutoFill注解的具体内容，是insert还是update
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        //获取方法的参数，如Employee对象，Category对象
        Object[] args = joinPoint.getArgs();//约定需要添加公共字段的实体放在方法的第一个参数
        Object entity = args[0];
        //为对象的公共字段的方法设置值
        Class aClass = entity.getClass();
        if(operationType == OperationType.INSERT){//四个公共字段都需要赋值
            try {
                //获得set方法
                Method setCreateTime = aClass.getMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setUpdateTime = aClass.getMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setCreateUser = aClass.getMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateUser = aClass.getMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                //赋值
                setCreateTime.invoke(entity, LocalDateTime.now());
                setUpdateTime.invoke(entity, LocalDateTime.now());
                setCreateUser.invoke(entity, BaseContext.getCurrentId());
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType == OperationType.UPDATE){//两个公共字段需要赋值
            try {
                Method setUpdateTime = aClass.getMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = aClass.getMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                setUpdateTime.invoke(entity, LocalDateTime.now());
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
