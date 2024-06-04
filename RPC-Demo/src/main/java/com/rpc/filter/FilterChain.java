package com.rpc.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qyingli
 * @date 2024/5/19 21:24
 * @package: com.rpc.filter
 * @description: TODO 责任链设计模式
 */

public class FilterChain {

    private List<filter> filters = new ArrayList<>();

    //重载, 两种添加方式
    public void addFilter(filter filter){
        filters.add(filter);
    }
    public void addFilter(List<Object> filters){
        for (Object filter : filters) {
            addFilter((filter) filter);
        }
    }

    //将过滤器链中的所有过滤器依次执行
    public void doFilter(FilterData data){
        for (filter filter : filters) {
            filter.doFilter(data);
        }
    }
}
