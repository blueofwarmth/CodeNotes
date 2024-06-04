package com.rpc.router;

/**
 * @author Qyingli
 * @date 2024/5/21 20:31
 * @package: com.rpc.router
 * @description: TODO 根据负载均衡获取对应的服务节点(负载均衡包装服务节点)
 */
public interface LoadBalencer {

    /**
     * 根据传入的参数和serviceName选择一个负载均衡元数据
     * @param params
     * @param serviceName
     * @return 当前服务节点以及其他服务节点
     */
    LoadBalenceMeta select(Object[] params ,String serviceName);

}
