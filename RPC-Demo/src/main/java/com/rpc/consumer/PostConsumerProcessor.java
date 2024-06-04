package com.rpc.consumer;

import com.rpc.annotation.RpcReference;
import com.rpc.config.RpcProperties;
import com.rpc.faultTol.FaultTolerantStrategyFactory;
import com.rpc.filter.FilterConfig;
import com.rpc.protocol.serialization.SerializationFactory;
import com.rpc.register.RegisterFactory;
import com.rpc.router.LoadBalenceFactory;
import com.rpc.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.index.RedisIndexDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author Qyingli
 * @date 2024/5/19 16:20
 * @package: com.rpc.consumer
 * @description: TODO 消息发送处理
 */
public class PostConsumerProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {
    Logger logger = LoggerFactory.getLogger(PostConsumerProcessor.class);
    RpcProperties rpcProperties;


    //读取配置文件
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties instance = RpcProperties.getInstance();
        //用Utils工具读取
        PropertiesUtils.init(instance, environment);
        rpcProperties = instance;
    }


    /**
     * 在 Spring 容器初始化 Bean 后，会调用该方法对每个 Bean 进行处理。
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有字段
        final Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历所有字段找到 RpcReference(调用方) 注解的字段
        for (Field field : fields) {
            //如果该字段标记了RpcReference注解, 则获取该字段的类型(调用方的类型)
            if(field.isAnnotationPresent(RpcReference.class)){
                final RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                final Class<?> refClass = field.getType();
                field.setAccessible(true);
                Object object = null;
                try {
                    // 为调用方创建代理对象
                    object = Proxy.newProxyInstance(
                            refClass.getClassLoader(),
                            new Class<?>[]{refClass},
                            new RpcInvokerProxy(
                                    rpcReference.serviceVersion(),
                                    rpcReference.timeout(),
                                    rpcReference.faultTolerant(),
                                    rpcReference.loadBalancer(),
                                    rpcReference.retryCount()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // 将代理对象设置给字段
                    //bean是实例, object是值
                    field.set(bean,object);
                    field.setAccessible(false);
                    logger.info(beanName + " field:" + field.getName() + "注入成功");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    logger.info(beanName + " field:" + field.getName() + "注入失败");
                }
            }
        }
        return bean;
    }

    /**
     * spi 用来初始化其中的bean
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SerializationFactory.init();
        RegisterFactory.init();
        LoadBalenceFactory.init();
        FilterConfig.initClientFilter();
        FaultTolerantStrategyFactory.init();
    }
}
