package com.atguigu.jdbc;

import java.sql.*;
import java.util.Scanner;

/**
 * @Author 赵伟风
 * Description: 使用预编译Statement解决注入攻击问题
 */
public class JdbcPreparedStatementLoginPart {


    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        //1.输入账号和密码
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入用户:");
        String account = scanner.nextLine();
        System.out.println("输入密码:");
        String password = scanner.nextLine();
        scanner.close();

        //2.jdbc的查询使用
        //a.注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        //b.获取连接
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu", "root", "qyingli001234");

        //c.创建preparedStatement传入SQL语句
        //connection.createStatement();
        //TODO 需要传入SQL语句结构
        //TODO 要的是SQL语句结构，动态值的部分使用 ? ,  占位符！
        //TODO ?  不能加 '?'  ? 只能替代值，不能替代关键字和容器名
        String sql = "select * from t_user where account = ? and password = ? ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //d.占位符赋值
        //给占位符赋值！ 从左到右，从1开始！
        /**
         *  int 占位符的下角标
         *  object 占位符的值
         */
        preparedStatement.setObject(1,account);
        preparedStatement.setObject(2,password);

        //这哥们内部完成SQL语句拼接！
        //e.执行SQL语句即可
        ResultSet resultSet = preparedStatement.executeQuery();
        //preparedStatement.executeUpdate()

        //进行结果集对象解析
        if (resultSet.next()){
            //只要向下移动，就是有数据 就是登录成功！
            System.out.println("登录成功！");
        }else{
            System.out.println("登录失败！");
        }

        //关闭资源
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

}
