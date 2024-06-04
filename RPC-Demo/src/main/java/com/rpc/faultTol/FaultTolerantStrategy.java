package com.rpc.faultTol;

import com.rpc.common.RpcResponse;
import org.apache.coyote.Response;

/**
 * @author Qyingli
 * @date 2024/5/21 21:12
 * @package: com.rpc.faultTol
 * @description: TODO 容错策略
 */

public interface FaultTolerantStrategy {

   Object handler(Object... args);
}
