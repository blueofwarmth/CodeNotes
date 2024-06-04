package com.rpc.faultTol;

import com.rpc.spi.ExtensionLoader;

/**
 * @author Qyingli
 * @date 2024/5/21 21:13
 * @package: com.rpc.faultTol
 * @description: TODO
 */
public class FaultTolerantStrategyFactory {
    public static FaultTolerantStrategy get(String faultTolService) throws Exception{
        return ExtensionLoader.getInstance().getBeanByKey(faultTolService);
    }
    public static void init() throws Exception {
        // 初始化策略
        ExtensionLoader.getInstance().loadExtension(FaultTolerantStrategy.class);

    }
}
