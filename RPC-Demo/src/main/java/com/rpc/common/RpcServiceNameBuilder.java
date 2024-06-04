package com.rpc.common;


/**
 * todo 用服务名和版本作为一个key
 */
public class RpcServiceNameBuilder {

    // key: 服务名 value: 服务提供方s
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }

}