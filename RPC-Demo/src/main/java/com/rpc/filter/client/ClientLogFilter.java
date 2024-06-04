package com.rpc.filter.client;

import com.rpc.filter.FilterData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qyingli
 * @date 2024/5/19 21:44
 * @package: com.rpc.filter.client
 * @description: TODO
 */
public class ClientLogFilter implements clientBeforeFilter{
    Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    public void doFilter(FilterData filterData) {
        logger.info(filterData.toString());
    }
}
