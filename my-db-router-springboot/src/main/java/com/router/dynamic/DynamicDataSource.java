package com.router.dynamic;

import com.router.DBContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author Qyingli
 * @date 2024/4/23 20:21
 * @package: com.router.dynamic
 * @description: TODO 动态获取数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

//    AbstractRoutingDataSource 主要是存放和设置数据源
    @Override
    protected Object determineCurrentLookupKey() {
        return "db" + DBContextHolder.getDBKey();
    }
}
