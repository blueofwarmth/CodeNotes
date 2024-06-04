package com.rpc.filter;

import com.rpc.filter.client.clientAfterFilter;
import com.rpc.filter.client.clientBeforeFilter;
import com.rpc.filter.service.serviceAfterFilter;
import com.rpc.filter.service.serviceBeforeFilter;
import com.rpc.spi.ExtensionLoader;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * @author Qyingli
 * @date 2024/5/19 21:47
 * @package: com.rpc.filter
 * @description: TODO 将所有拦截器初始化
 */
@Data
public class FilterConfig {

    private static FilterChain serviceBeforeFilterChain = new FilterChain();
    private static FilterChain serviceAfterFilterChain = new FilterChain();
    private static FilterChain clientBeforeFilterChain = new FilterChain();
    private static FilterChain clientAfterFilterChain = new FilterChain();

    public static FilterChain getClientAfterFilterChain() {
        return clientAfterFilterChain;
    }
    public static FilterChain getClientBeforeFilterChain() {
        return clientBeforeFilterChain;
    }
    public static FilterChain getServiceAfterFilterChain() {
        return serviceAfterFilterChain;
    }
    public static FilterChain getServiceBeforeFilterChain() {
        return serviceBeforeFilterChain;
    }


    @SneakyThrows
    public static void initServiceFilter() {
        //类加载器
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(serviceAfterFilter.class);
        extensionLoader.loadExtension(serviceBeforeFilter.class);


        serviceBeforeFilterChain.addFilter(extensionLoader.getBeanByClass(serviceBeforeFilter.class));
        serviceAfterFilterChain.addFilter(extensionLoader.getBeanByClass(serviceAfterFilter.class));
    }

    @SneakyThrows
    public static void initClientFilter() throws IOException, ClassNotFoundException {
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(clientAfterFilter.class);
        extensionLoader.loadExtension(clientBeforeFilter.class);
        clientBeforeFilterChain.addFilter(extensionLoader.getBeanByClass(clientBeforeFilter.class));
        clientAfterFilterChain.addFilter(extensionLoader.getBeanByClass(clientAfterFilter.class));
    }
}
