package com.rpc.faultTol;

import com.rpc.common.RpcResponse;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qyingli
 * @date 2024/5/21 21:13
 * @package: com.rpc.faultTol
 * @description: TODO 快速失败
 */
public class FailFast implements FaultTolerantStrategy{
    private Logger log = LoggerFactory.getLogger(FailFast.class);

    @Override
    public Object handler(Object... args) {
        RpcResponse rpcResponse = (RpcResponse) args[0];
        String errorMsg = (String) args[1];
        log.warn("rpc 调用失败,触发 FailFast 策略,异常信息: {}",errorMsg);
        return rpcResponse.getException();
    }
}
