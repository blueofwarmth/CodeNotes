package com.database.api.utils;

import javax.activation.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/*
* 1.0版本
* 内部包含一个连接池对象, 并且对外提供连接和回收连接的方法
* 写成静态, 调用方便
* 实现:
* 属性 连接池对象
*       单例模式
*       static {
*        全局调用一次
* }
*      对外提供连接的方法
*      回收外部传来的方法
* */
public class JDBCUtils {
    private static DataSource dataSource = null; // 连接池对象
    static {
        //初始化连接池对象
        Properties properties = new Properties();
        //导入Properties文件内容
        InuptStream  ips = JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties");
        try {
            properties.load(ips);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // 对外提供连接的方法
    public static Connection getConnection()  throws SQLException {
        return dataSource.getConnection();
    }
    public static void FreeConnection(Connection connection) throws SQLException {
        connection.close(); //连接池的连接, 调用close回收
    }


}
