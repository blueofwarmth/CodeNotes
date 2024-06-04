package com.router.config;

import com.router.DBRouterConfig;
import com.router.DBRouterJoinPoint;
import com.router.DBRouterPoint;
import com.router.dynamic.DynamicDataSource;
import com.router.dynamic.DynamicInterceptor;
import com.router.strategy.IDBRouterStrategy;
import com.router.strategy.impl.IDBRouterStrategyImpl;
import com.router.util.PropertyUtil;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qyingli
 * @date 2024/4/23 20:12
 * @package: com.router.config
 * @description: TODO 数据源配置解析以及注入一些bean
 */
public class DataSourceAutoConfig implements EnvironmentAware {

    //数据源配置组
    //value：数据源详细信息
    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();
    //默认数据源配置
    private Map<String, Object> defaultDataSourceConfig;
    //分库数量
    private int dbCount;
    //分表数量
    private int tbCount;
    //路由字段
    private String routerKey;

    /**
     * AOP，用于分库
     *
     * @param dbRouterConfig
     * @param dbRouterStrategy
     * @return
     */
    @Bean
    public DBRouterPoint dbRouterPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        return dbRouterPoint(dbRouterConfig, dbRouterStrategy);
    }

    /**
     * 配置插件bean,用于动态的决定表信息
     *
     * @return
     */
    @Bean
    public DynamicInterceptor dynamicInterceptor() {
        return new DynamicInterceptor();
    }

    /**
     * 路由策略
     * @param dbRouterConfig
     * @return
     */
    @Bean
    public IDBRouterStrategy dbRouterStrategy(DBRouterConfig dbRouterConfig) {
        return new IDBRouterStrategyImpl(dbRouterConfig);
    }

    /**
     * 将DB的信息注入到spring中，供后续获取
     *
     * @return
     */
    @Bean
    public DBRouterConfig dbRouterConfig() {
        return new DBRouterConfig(dbCount, tbCount, routerKey);
    }




    /**
     * 用于配置 TargetDataSources 以及 DefaultTargetDataSource
     * TargetDataSources: 额外的数据源
     * 可以用指定的key获取其他的数据源来达到动态切换数据源
     * DefaultTargetDataSource: 默认的数据源
     * 如果没有要用的数据源就会使用默认的数据源
     *
     * @return
     */
    public DynamicDataSource dynamicDataSource() {
        HashMap<Object, Object> targetDataSources = new HashMap<>();
        for (String mapKey : dataSourceMap.keySet()) {
            Map<String, Object> objectObjectHashMap = dataSourceMap.get(mapKey);
            //切换数据源
            targetDataSources.put(mapKey, new DriverManagerDataSource(objectObjectHashMap.get("url").toString(), objectObjectHashMap.get("username").toString(), objectObjectHashMap.get("password").toString()));
        }
        //设置数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        //默认数据源
        dynamicDataSource.setDefaultTargetDataSource(new DriverManagerDataSource(defaultDataSourceConfig.get("url").toString(), defaultDataSourceConfig.get("username").toString(), defaultDataSourceConfig.get("password").toString()));

        return dynamicDataSource;
    }



    /**
     * 读取yml中的数据源信息
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "my-db-router.jdbc.datasource.";

        dbCount = Integer.valueOf(environment.getProperty(prefix + "dbCount"));
        tbCount = Integer.valueOf(environment.getProperty(prefix + "tbCount"));
        routerKey = environment.getProperty(prefix + "routerKey");

        // 分库分表数据源
        String dataSources = environment.getProperty(prefix + "list");
        assert dataSources != null;
        for (String dbInfo : dataSources.split(",")) {
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
            dataSourceMap.put(dbInfo, dataSourceProps);
        }

        // 默认数据源
        String defaultData = environment.getProperty(prefix + "default");
        defaultDataSourceConfig = PropertyUtil.handle(environment, prefix + defaultData, Map.class);

    }

    /**
     * 配置事务
     * @param dataSource
     * @return
     */
    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }
}
