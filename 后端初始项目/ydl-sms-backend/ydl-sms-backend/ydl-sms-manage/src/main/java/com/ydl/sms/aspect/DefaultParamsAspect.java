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
        System.out.println("走到切面方法中！");

        //通过方法参数实体类 有没有id来判断 是新增还是修改
        //threadlocal  中获取当前登录人的userId
        Long userId = BaseContextHandler.getUserId();   //这个Handler类的封装，人家打包为jar包导入了

        //拿这个方法(面向切面，适配每一个Controller的方法)的参数
        Object[] args = point.getArgs();
        for (Object arg : args) {
            Class<?> classes = arg.getClass(); //参数类型 string 签名类型
            Method method = getMethod(classes, "getId");  //getID，没参数，所以不用写这里的type
            Object id=null;
            if(method!=null){
                id=method.invoke(arg);       //这里是反射的invoke意思，大体是调用该方法的id，可以再去学习下反射的invoke
            }

            //1.1 新增  创建人 创建时间 修改人 修改时间
            if(id==null){   //因为新增没有id，他会重新创建，所以id为null
                method=getMethod(classes,"setCreateUser",String.class); //设置创建人的方法，即拿到了setCreateUser方法
                if(method!=null){
                    method.invoke(arg, userId.toString());   //这里用arg是获取的对应方法的参数
                }
                //entity.setCreateTime(LocalDateTime.MAX);  同上理论
                method=getMethod(classes,"setCreateTime",LocalDateTime.class);
                if(method!=null){
                    method.invoke(arg, LocalDateTime.now());
                }
            }

            //1.2 修改  修改人 修改时间
            method=getMethod(classes,"setUpdateUser",String.class); //设置创建人的方法
            if(method!=null){
                method.invoke(arg, userId.toString());
            }
            method=getMethod(classes,"setUpdateTime",LocalDateTime.class);
            if(method!=null){
                method.invoke(arg, LocalDateTime.now());
            }
        }

    }

    /**
     * 获得方法对象
     * @param classes
     * @param name 方法名
     * @param types 参数类型
     * @return
     */
    private Method getMethod(Class classes, String name, Class... types) {
        try {
            return classes.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
