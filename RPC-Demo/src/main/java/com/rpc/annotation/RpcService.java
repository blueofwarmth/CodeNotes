package com.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qyingli
 * @date 2024/5/15 14:11
 * @package: com.rpc.annotation
 * @description: TODO Rpc服务提供方
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 指定实现方,默认为实现接口中第一个
     *
     * @return
     */
    Class<?> serviceInterface() default void.class;

    /**
     * 版本
     * @return
     */
    String version() default "1.0";
}
