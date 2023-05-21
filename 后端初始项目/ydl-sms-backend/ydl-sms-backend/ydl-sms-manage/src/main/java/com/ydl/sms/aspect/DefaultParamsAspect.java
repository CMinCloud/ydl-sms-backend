package com.ydl.sms.aspect;

import com.ydl.context.BaseContextHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 通过切面方式，自定义注解，实现实体基础数据的注入（创建者、创建时间、修改者、修改时间）
 * 使用这个切面是方法中只要加入了@DefaultParams注解就会使用这个切面
 */
@Component //交给spring
@Aspect   //表示这个类是切面
@Slf4j
public class DefaultParamsAspect {
    @SneakyThrows
    @Before("@annotation(com.ydl.sms.annotation.DefaultParams)")
    public void beforeEvent(JoinPoint point) {

        // TODO 自动注入基础属性（创建者、创建时间、修改者、修改时间）
        /*获取当前登录对象的参数*/
        Long userId = BaseContextHandler.getUserId();
        Object[] args = point.getArgs();
        for (Object arg : args) {
            /*1、根据方法参数实体,根据是否传了id判断是新增还是修改*/
            Class<?> aClass = arg.getClass();
            Method method = getMethod(aClass, "getId");
            Object id = null;
            if (Objects.nonNull(method)) {
                id = method.invoke(arg);
            }
            /*1，1新增 创建人 创建时间 修改者 修改时间*/
            LocalDateTime now = LocalDateTime.now();
            if (Objects.isNull(id)) {
                method = getMethod(aClass, "setCreateUser", String.class);
                if (Objects.nonNull(method)) method.invoke(arg, userId.toString());
                method = getMethod(aClass, "setCreateTime", LocalDateTime.class);
                if (Objects.nonNull(method)) method.invoke(arg, now);
            }
            /*1.2 修改 修改者 修改时间*/
            method = getMethod(aClass, "setUpdateUser", String.class);
            if (Objects.nonNull(method)) method.invoke(arg, userId.toString());

            method = getMethod(aClass, "setUpdateTime", LocalDateTime.class);
            if (Objects.nonNull(method)) method.invoke(arg, now);
        }


    }

    /**
     * 获得方法对象
     *
     * @param classes
     * @param name    方法名
     * @param types   参数类型
     * @return
     */
    private Method getMethod(Class classes, String name, Class... types) {
        Method method = null;
        try {
            method = classes.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;

    }
}
