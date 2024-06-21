package com.database.api.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.activation.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

    //TODO: 利用线程本地变量, 存储连接信息, 确保一个线程的多个方法可以获取同于一个连接
    //优势:事务操作的时候, service和dao都是同一个线程, 获取同一个连接, 不用再传递参数
public class JDBCUtilsV2<InuptStream> {
    private static DataSource dataSource = null; // 连接池对象
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

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
        //线程本地变量中是否存有连接
        Connection connection = threadLocal.get();

        if (connection == null) {
            connection = dataSource.getConnection();
            threadLocal.set(connection);
        }
        return connection;
    }
    public static void FreeConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if(connection != null)
        {
            //清空线程本地变量
            threadLocal.remove();
            //回归线程本地变量状态
            connection.setAutoCommit(true);
            connection.close(); //连接池的连接, 调用close回收
        }
    }
}
