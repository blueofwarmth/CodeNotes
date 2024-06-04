package com.rpc.router;

import com.rpc.common.ServiceMeta;
import com.rpc.config.RpcProperties;
import com.rpc.register.RegisterService;
import com.rpc.spi.ExtensionLoader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalencer {

    private static AtomicInteger roundRobinId = new AtomicInteger(0);

    @Override
    public LoadBalenceMeta select(Object[] params, String serviceName) {
        // 获取注册中心
        RegisterService registryService = ExtensionLoader.getInstance().get(RpcProperties.getInstance().getRegisterType());
        List<ServiceMeta> discoveries = registryService.getService(serviceName);
        // 1.获取所有服务
        int size = discoveries.size();
        // 2.根据当前轮询ID取余服务长度得到具体服务
        roundRobinId.addAndGet(1);
        if (roundRobinId.get() == Integer.MAX_VALUE){
            roundRobinId.set(0);
        }

        return LoadBalenceMeta.build(discoveries.get(roundRobinId.get() % size),discoveries);
    }

}
