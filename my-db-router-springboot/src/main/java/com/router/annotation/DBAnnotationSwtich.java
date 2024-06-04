package com.router.annotation;

import java.lang.annotation.*;

/**
 * @author Qyingli
 * @date 2024/4/27 15:44
 * @package: com.router.annotation
 * @description: TODO 是否开启分库分表
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBAnnotationSwtich {
    boolean swtich() default false;
}
