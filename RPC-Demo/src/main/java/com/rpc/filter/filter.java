package com.rpc.filter;

import com.rpc.common.RpcRequest;

/**
 * @author Qyingli
 * @date 2024/5/19 21:19
 * @package: com.rpc.filter
 * @description: TODO 拦截器抽象接口
 */

public interface filter {

    void doFilter(FilterData filterData);
}
