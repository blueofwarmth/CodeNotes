package com.rpc.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Qyingli
 * @date 2024/5/15 13:56
 * @package: com.rpc.annotation
 * @description: TODO 开启自动装配, 与springboot进行整合, 一般为后置处理器
 */

/*
用于指定其他注解可以应用的程序元素类型。它定义了一个注解可以使用的位置，从而限制了注解的使用范围。
ElementType 是一个枚举数组, 包含多种类型
ElementType.TYPE: 适用于类、接口（包括注解类型）或枚举
*/
@Target({ElementType.TYPE})
/*
它定义了一个注解的生命周期，即该注解在何时可用
注解在运行时保留，因此可以通过反射机制读取注解信息。
常用于需要在运行时处理注解的场景，例如依赖注入框架（如 Spring）或自定义序列化逻辑。
*/
@Retention(RetentionPolicy.RUNTIME)
@Import(ConsumerPostProcessor.class)
public @interface EnableConsumerRpc {


}
