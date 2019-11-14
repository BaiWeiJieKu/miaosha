package com.imooc.miaosha.access;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义接口防刷注解
 * 规定访问接口是否需要登录，几秒内能访问几次
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
    /**
     * 时间范围
     *
     * @return
     */
    int seconds();

    /**
     * 时间范围内最大点击数
     *
     * @return
     */
    int maxCount();

    /**
     * 是否需要登录，默认需要
     *
     * @return
     */
    boolean needLogin() default true;
}
