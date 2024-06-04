package com.router.annotation;

import java.lang.annotation.*;

/**
 * @author Qyingli
 * @date 2024/4/27 15:43
 * @package: com.router.annotation
 * @description: TODO 分库分表注解
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBAnnotation {
    String key() default "";
}
