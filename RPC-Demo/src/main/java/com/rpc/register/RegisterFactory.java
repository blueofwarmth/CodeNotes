package com.rpc.register;

import com.rpc.spi.ExtensionLoader;

/**
 * @author Qyingli
 * @date 2024/5/17 14:56
 * @package: com.rpc.register
 * @description: TODO
 */
public class RegisterFactory {
    /**
     * 得到注册服务
     * @param registryService
     * @return
     * @throws Exception
     */
    public static Register get(String registryService) throws Exception {
        return ExtensionLoader.getInstance().getBeanByKey(registryService);
    }
    public static void init() throws Exception{
        ExtensionLoader.getInstance().loadExtension(RegisterService.class);
    }
}

