package com.database.api.expand.transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import static java.lang.Class.forName;

public class BankDao {
    public void addBank(String account, int money, Connection connection) throws Exception {
        //1. 注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        //2. 获取连接
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu", "root", "qyingli001234");
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguitu?user=root?password=qyingli001234");
        //3. 编写SQL语句
        String sql = "update t_bank set money = money + ? where account = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //4. 占位符赋值
        preparedStatement.setInt(1, money);
        preparedStatement.setString(2, account);

        //5.发送SQL语句
        int i = preparedStatement.executeUpdate();
        //6. 关闭资源
        preparedStatement.close();
        System.out.println("added money");
    }

    public void subBank(String account, int money, Connection connection) throws Exception{
        1. 注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        2. 获取连接
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu", "root", "qyingli001234");
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguitu?user=root?password=qyingli001234");
        //3. 编写SQL语句
        String sql = "update t_bank set money = money - ? where account = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //4. 占位符赋值
        preparedStatement.setInt(1, money);
        preparedStatement.setString(2, account);

        //5.发送SQL语句
        int i = preparedStatement.executeUpdate();
        //6. 关闭资源
        preparedStatement.close();
        System.out.println("subed money");
    }
}
