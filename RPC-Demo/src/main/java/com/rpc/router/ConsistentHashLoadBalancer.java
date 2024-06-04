package com.rpc.router;

import com.rpc.common.ServiceMeta;
import com.rpc.config.RpcProperties;
import com.rpc.register.RegisterService;
import com.rpc.spi.ExtensionLoader;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希
 */
public class ConsistentHashLoadBalancer implements LoadBalencer {

    // 物理节点映射的虚拟节点,为了解决哈希倾斜
    private final static int VIRTUAL_NODE_SIZE = 10;
    private final static String VIRTUAL_NODE_SPLIT = "$";

    @Override
    public LoadBalenceMeta select(Object[] params, String serviceName) {
        // 获取注册中心
        RegisterService registryService = ExtensionLoader.getInstance().getBeanByKey(RpcProperties.getInstance().getRegisterType());
        //拿到对应服务
        List<ServiceMeta> discoveries = registryService.getService(serviceName);

        //分配节点
        final ServiceMeta curServiceMeta = allocateNode(makeConsistentHashRing(discoveries), params[0].hashCode());
        return LoadBalenceMeta.build(curServiceMeta,discoveries);
    }


    private ServiceMeta allocateNode(TreeMap<Integer, ServiceMeta> ring, int hashCode) {
        // 获取最近的哈希环上节点位置
        Map.Entry<Integer, ServiceMeta> entry = ring.ceilingEntry(hashCode);
        if (entry == null) {
            // 如果没有找到则使用最小的节点
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 将所有服务实例添加到一致性哈希环上，并生成虚拟节点
     * 这里每次调用都需要构建哈希环是为了扩展(服务提供方)
     * @param servers 服务实例列表
     * @return 一致性哈希环
     */
    private TreeMap<Integer, ServiceMeta> makeConsistentHashRing(List<ServiceMeta> servers) {
        TreeMap<Integer, ServiceMeta> ring = new TreeMap<>();
        for (ServiceMeta instance : servers) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
            }
        }
        return ring;
    }

    /**
     * 根据服务实例信息构建缓存键
     * @param serviceMeta
     * @return
     */
    private String buildServiceInstanceKey(ServiceMeta serviceMeta) {

        return String.join(":", serviceMeta.getServiceAddr(), String.valueOf(serviceMeta.getServicePort()));
    }
}
