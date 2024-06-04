package com.rpc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RpcRequest implements Serializable {

    private String serviceVersion;
    private String className;
    private String methodName;
    private Object data;
    private Class dataClass;
    private Class<?>[] parameterTypes;
    private Map<String,Object> serviceAttachments; //服务附件
    private Map<String,Object> clientAttachments; //客户附件
}
