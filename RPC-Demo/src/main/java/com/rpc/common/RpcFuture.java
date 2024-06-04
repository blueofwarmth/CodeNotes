package com.rpc.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @author Qyingli
 * @date 2024/5/15 14:34
 * @package: com.rpc.common.constants
 * @description: TODO future代表了一个异步操作的结果。它提供了一种机制，用于处理和管理 I/O 操作，
 */
@Data
public class RpcFuture<T> {
    //Promise 表示一个异步操作的结果
    private Promise<T> promise;
    private long timeout;

}
