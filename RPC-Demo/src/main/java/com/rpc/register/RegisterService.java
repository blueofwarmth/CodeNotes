package com.rpc.register;

import com.rpc.common.ServiceMeta;

import java.io.IOException;
import java.util.List;

/**
 * @author Qyingli
 * @date 2024/5/17 10:38
 * @package: com.rpc.register
 * @description: TODO
 */

public interface RegisterService {
    /**
     * 服务注册
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务销毁
     * @param serviceMeta
     * @throws Exception
     */
    void unregister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 通过serviceName获取
     * @param serviceName
     * @return
     * @throws Exception
     */
    List<ServiceMeta> getService(String serviceName);

    /**
     * 服务关闭
     * @param serviceMeta
     * @throws Exception
     */
    void close(ServiceMeta serviceMeta) throws IOException;
}

