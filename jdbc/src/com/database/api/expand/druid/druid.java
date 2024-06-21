package com.database.api.expand.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.activation.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class druid {
    /**
     * 创建druid连接池对象，使用硬编码进行核心参数设置！
     *   必须参数： 账号
     *             密码
     *             url
     *             driverClass
     *   非必须参数：
     *           初始化个数
     *           最大数量等等  不推荐设置
     注意他要做的，注册驱动，获取连接，规定最大数量
     直接使用代码设置连接池连接参数方式
     */
    @Test
    public void druidHard() throws SQLException {
        //1.连接池对象
        DruidDataSource dataSource = new DruidDataSource();

        //2.设置四个必须参数
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setUrl("jdbc:mysql:///day01");
        //非必须
        dataSource.setInitialSize(5); //初始化数量
        dataSource.setMaxActive(10); //最大数量

        //3.获取连接
        Connection connection = dataSource.getConnection();
        // JDBC的步骤 正常curd
        //4.回收连接
        connection.close();
    }
    /**
     * 不直接在java代码编写配置文件！
     * 利用工厂模式，传入配置文件对象，创建连接池！
     * @throws Exception
     */
    @Test

//druid.properties直接放在src目录下
    public void druidSoft() throws Exception {
        //读取外部配置文件
        Properties properties = new Properties();
        //在src路径下的文件, 可以使用类加载器提供的方法
        InputStream ips = DruidDemo.class.getClassLoader().getResourceAsStream("druid.properties");
        properties.load(ips);
        //创建连接池
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();
        //数据库curd


        //关闭
        connection.close();
    }


}
