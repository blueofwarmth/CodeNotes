package com.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 消息
 */
@Data
public class RpcProtocol<T> implements Serializable {

    private MsgHeader header;
    private T body;
}
