package com.rpc.router;

import com.rpc.spi.ExtensionLoader;

/**
 * @author Qyingli
 * @date 2024/5/21 21:00
 * @package: com.rpc.router
 * @description: TODO
 */
public class LoadBalenceFactory {
    public static LoadBalencer  get(String loadServiceName) throws Exception{
        return ExtensionLoader.getInstance().getBeanByKey(loadServiceName);
    }
    public static void init() throws Exception{
        ExtensionLoader.getInstance().loadExtension(LoadBalencer.class);
    }
}
