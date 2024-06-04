package com.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qyingli
 * @date 2024/5/15 14:18
 * @package: com.rpc.annotation
 * @description: TODO 服务调用方注解
 * 定义版本, 超时时间, 负载均衡, 容错机制, 重试次数
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    /**
     * 版本
     * @return
     */
    String serviceVersion() default "1.0";

    /**
     * 超时时间
     * @return
     */
    long timeout() default 5000;

    /**
     * 可选的负载均衡:consistentHash,roundRobin...
     * @return
     */
    String loadBalancer() default LoadBalancerRules.RoundRobin;

    /**可选的容错策略:failover,failFast,failsafe...
     * @return
     */
    String faultTolerant() default FaultTolerantRules.FailFast;

    /**
     * 重试次数
     * @return
     */
    long retryCount() default 3;
}
