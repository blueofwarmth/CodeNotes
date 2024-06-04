package com.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qyingli
 * @date 2024/5/15 14:07
 * @package: com.rpc.annotation
 * @description: TODO 属性后缀
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertiesField {
    /**
     * 默认为属性名
     * 例如: registryType = registry-type  遵守配置文件规则
     * @return
     */
    String Value() default "";
}
