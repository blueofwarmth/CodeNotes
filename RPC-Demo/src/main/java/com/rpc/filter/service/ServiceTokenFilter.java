package com.rpc.filter.service;

import com.rpc.filter.FilterData;

import java.util.Map;

/**
 * @author Qyingli
 * @date 2024/5/19 21:41
 * @package: com.rpc.filter.service
 * @description: TODO token验证拦截器
 */
public class ServiceTokenFilter implements serviceBeforeFilter{
    @Override
    public void doFilter(FilterData filterData) {
        // 实现过滤逻辑
        // 检查请求中是否包含有效的token
        Map<String, Object> serviceAttachments = filterData.getServiceAttachments();
        Map<String, Object> clientAttachments = filterData.getClientAttachments();
        //如果不相同
        if(!serviceAttachments.getOrDefault("token", "").equals(clientAttachments.getOrDefault("token", ""))) {
            throw new IllegalArgumentException("token不正确");
        }
    }
}
