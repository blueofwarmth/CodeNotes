package com.rpc.common;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO 将请求发送到对应的服务
 */
public class RpcRequestHolder {

    // 请求id
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    // 绑定请求
    public static final Map<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>(); //并发HashMap
}