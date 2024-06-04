package com.rpc.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private Object data;
    private Class dataClass;
    private String message;
    private Exception exception;
}
