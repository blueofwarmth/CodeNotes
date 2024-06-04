package com.rpc.faultTol;

import com.rpc.common.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qyingli
 * @date 2024/5/22 15:06
 * @package: com.rpc.faultTol
 * @description: TODO 忽略错误
 */
public class FailSafe implements FaultTolerantStrategy{
    Logger log = LoggerFactory.getLogger(FailSafe.class);

    @Override
    public Object handler(Object... args) {
        log.warn("FailSafe strategy: ignoring error and returning null");
        return null;
    }
}
