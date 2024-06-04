package com.rpc.filter;

import com.rpc.common.RpcResponse;
import lombok.Data;

import java.util.Map;

/**
 * note 拦截器上下文数据
 */
@Data
public class FilterData {

    private String serviceVersion;
    private long timeout;
    private long retryCount;
    private String className;
    private String methodName;
    private Object args;
    //附件
    private Map<String,Object> serviceAttachments;
    private Map<String,Object> clientAttachments;
    private RpcResponse data; // 执行业务逻辑后的数据
    public FilterData(){}
}