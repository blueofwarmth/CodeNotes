package com.rpc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ServiceMeta implements Serializable {
    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

    /**
     * 关于redis注册中心的属性
     */
    private long endTime;

    private String UUID;

    /**
     * 判断服务元数据是否相等
     * 因为故障转移需要移除不可用服务
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceMeta that = (ServiceMeta) o;
        return servicePort == that.servicePort &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(serviceVersion, that.serviceVersion) &&
                Objects.equals(serviceAddr, that.serviceAddr) &&
                Objects.equals(UUID, that.UUID);
    }
}