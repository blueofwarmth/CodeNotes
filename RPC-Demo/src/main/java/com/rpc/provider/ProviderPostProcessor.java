package com.rpc.provider;

import com.rpc.annotation.RpcService;
import com.rpc.common.RpcServiceNameBuilder;
import com.rpc.common.ServiceMeta;
import com.rpc.config.RpcProperties;
import com.rpc.register.RegisterFactory;
import com.rpc.register.RegisterService;
import com.rpc.utils.PropertiesUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务提供方后置处理器
 * 1. 服务启动
 *     1. 配置netty
 *     2. 初始化相关组件
 * 2. 服务注册
 *
 */
public class ProviderPostProcessor implements
        InitializingBean,   //初始化bean
        BeanPostProcessor, //后置处理器
        EnvironmentAware //读取配置文件信息
{


    private Logger logger = LoggerFactory.getLogger(ProviderPostProcessor.class);
    //配置文件
    RpcProperties rpcProperties;

    private static String serverAddress = "127.0.0.1";
    //本地缓存
    private final Map<String, Object> rpcServiceMap = new HashMap<>();
/*********************************************/

    //启动服务
    @Override
    public void afterPropertiesSet() throws Exception {

        Thread t = new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                logger.error("start rpc server error.", e);
            }
        });
        t.setDaemon(true);
        t.start();
        SerializationFactory.init();
        RegisterFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initServiceFilter();
        ThreadPollFactory.setRpcServiceMap(rpcServiceMap);
    }

    //配置netty服务
    private void startRpcServer() throws InterruptedException {
        int serverPort = rpcProperties.getPort();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //编解码, 拦截器, 请求处理器
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new ServiceBeforeFilterHandler())
                                    .addLast(new RpcRequestHandler())
                                    .addLast(new ServiceAfterFilterHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //端口, 地址
            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, serverPort).sync();
            channelFuture.channel().closeFuture().sync();
            //添加一个关闭钩子（shutdown hook）来确保在应用程序退出之前进行必要的资源清理
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }, "Allen-thread"));
        } finally {
            //优雅关闭
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 服务注册
     * 反射获取服务信息
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //获取Class类
        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService(提供方) 注解的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 可能会有多个接口,默认选择第一个接口
            //服务提供方和调用方使用的接口名得对应
            String serviceName = beanClass.getInterfaces()[0].getName();
            //接口不为空
            if (!rpcService.serviceInterface().equals(void.class)){
                serviceName = rpcService.serviceInterface().getName();
            }
            String serviceVersion = rpcService.version();

            try {
                // 服务注册
                int servicePort = rpcProperties.getPort();
                // 获取注册中心到ioc中
                RegisterService registryService = RegisterFactory.get(rpcProperties.getRegisterType());
                ServiceMeta serviceMeta = new ServiceMeta();
                // 服务提供方地址
                serviceMeta.setServiceAddr("127.0.0.1");
                serviceMeta.setServicePort(servicePort);
                serviceMeta.setServiceVersion(serviceVersion);
                serviceMeta.setServiceName(serviceName);
                //服务注册
                registryService.register(serviceMeta);
                // 缓存
                String key = RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
                rpcServiceMap.put(key, bean);
                logger.info("register server {} version {}",serviceName,serviceVersion);
            } catch (Exception e) {
                logger.error("failed to register service {}",  serviceVersion, e);
            }
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
        rpcProperties = properties;
        logger.info("读取配置文件成功");
    }
}