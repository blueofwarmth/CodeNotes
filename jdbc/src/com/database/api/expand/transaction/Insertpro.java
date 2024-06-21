package com.database.api.expand.transaction;

import org.junit.Test;

import java.sql.*;

public class Insertpro {
    /**
     * 插入10000条用户数据!
     * 账号: test
     * 密码: test
     * 昵称: 测试
     */
    @Test
    public void testInsert() throws Exception {

        // 注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 获取连接
        //TODO: 追加ewritBatchedStatements=true
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu?rewriteBatchedStatements=true",
                "root",
                "qyingli001234");

        // TODO: 切记, ? 只能代替 值!!!!! 不能代替关键字 特殊符号 容器名
        //TODO: 批量添加时values后面没有";"号
//        String sql = "insert into t_user(account,password,nickname) values (?,?,?)";
        String sql = "delete from t_user where account = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);


        // 占位符赋值
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            preparedStatement.setString(1, "test3"+i);//字符需要引号, 基本量直接写. 这样每次插入的值不同
//            preparedStatement.setString(2, "test3"+i);
            preparedStatement.addBatch();//不执行, 追加到上面的values后面
        }
        //遍历完毕后再最终执行
        preparedStatement.executeBatch();
        long end = System.currentTimeMillis();
        // 输出结果
        System.out.println("执行时间:" + (end - start));

        // 关闭资源close
        preparedStatement.close();
        connection.close();
    }


    @Test
    //使用批量插入优化后
    public void testInsertPro() throws Exception {

        // 注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 获取连接
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu", "root", "qyingli001234");

        // TODO: 切记, ? 只能代替 值!!!!! 不能代替关键字 特殊符号 容器名
        String sql = "insert into t_user(account,password,nickname) values (?,?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // 占位符赋值
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {


            preparedStatement.setString(1, "test3"+i);//字符需要引号, 基本量直接写. 这样每次插入的值不同
            preparedStatement.setString(2, "test3"+i);
            preparedStatement.setString(3, "ikun"+i);
            preparedStatement.executeUpdate();
        }
        long end = System.currentTimeMillis();
        // 输出结果
        System.out.println("执行时间:" + (end - start));

        // 关闭资源close
        preparedStatement.close();
        connection.close();
    }
}
