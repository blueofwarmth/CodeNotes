package com.rpc.protocol.serialization;

import java.io.IOException;

/**
 * todo 序列化接口
 */
public interface RpcSerialization {
    //<T>用于指定泛型类型
    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
