package com.database.api.expand.transactionV2;

import com.database.api.utils.JDBCUtilsV2;
import org.junit.Test;

import java.sql.Connection;

public class BankService {

    @Test
    public void start() throws Exception {
        transfer("jack", "sam", 3000);
    }

    public void transfer(String addAccount, String subAccount, int amount) throws Exception {
        //注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");
        //获取连接
//        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu", "root", "qyingli001234");

        Connection connection = JDBCUtilsV2.getConnection();

        BankDao dao = new BankDao();
        //一个事务最基本的要求, 必须是同一个连接对象 connection
        //一个转账就属于一个事务
        try {
            //开启事务
            //关闭事务提交
            connection.setAutoCommit(false);

            //执行数据库动作
            dao.addBank(addAccount, amount);
            System.out.println("--------------------------------");
            dao.subBank(subAccount, amount);
        } catch (Exception e) {
            //事务回滚
            connection.rollback();
            throw e;
        } finally {
            JDBCUtilsV2.FreeConnection();
        }
    }
}
