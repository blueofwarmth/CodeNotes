package com.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qyingli
 * @date 2024/5/15 14:09
 * @package: com.rpc.annotation
 * @description: TODO 属性前缀
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertiesPrefix {
    String Value();
}
