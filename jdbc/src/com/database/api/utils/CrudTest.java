package com.database.api.utils;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 赵伟风
 *         Description: 再次加强curd练习
 */
public class CrudTest extends BaseDao {

    /**
     * 插入一条用户数据!
     * 账号: test
     * 密码: test
     * 昵称: 测试
     */
    @Test
    public void testInsert() throws Exception {

        String sql = "insert into t_user(account,password,nickname) values (?,?,?);";
        int rows = executeUpdate(sql, "test222","222", "rose");
        // 输出结果
        if (rows > 0) {
            System.out.println(rows);
            //一行一列的数据！里面就装主键值！ id=值, 固定用getGeneratedKeys
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            //移动光标到第一行(默认第0行)
            resultSet.next();
            //获取第一列的数据
            int anInt = resultSet.getInt(1);//指向第一列, 对应的是ID
            System.out.println("anInt = " + anInt);
        }
    }

    /**
     * 修改一条用户数据!
     * 修改账号: test的用户,将nickname改为tomcat
     */
//    @Test
    public void testUpdate() throws Exception {
        String sql = "update t_user set nickname = ? where account = ? ;";
        // 发送SQL语句
        int rows = executeUpdate(sql,"kert", "test");
        // 输出结果
        System.out.println(rows);
    }

    /**
     * 删除一条用户数据!
     * 根据账号: test
     */
//    @Test
    public void testDelete() throws Exception {
        String sql = "delete from t_user where id = ? ;";
        // 发送SQL语句
        int rows = executeUpdate(sql,"2");

        // 输出结果
        System.out.println(rows);

        // 关闭资源close
        preparedStatement.close();
        connection.close();
    }

    /**
     * 查询全部数据!
     * 将数据存到List<Map>中
     * map -> 对应一行数据
     * map key -> 数据库列名或者别名
     * map value -> 数据库列的值
     * TODO: 思路分析
     * 1.先创建一个List<Map>集合
     * 2.遍历resultSet对象的行数据
     * 3.将每一行数据存储到一个map对象中!
     * 4.将对象存到List<Map>中
     * 5.最终返回
     *
     * TODO:
     * 初体验,结果存储!
     * 学习获取结果表头信息(列名和数量等信息)
     */
    @Test
    public void testQueryMap() throws Exception {

        // 注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 获取连接
        Connection connection = DriverManager.getConnection("jdbc:mysql:///atguigu", "root", "qyingli001234");

        // TODO: 切记, ? 只能代替 值!!!!! 不能代替关键字 特殊符号 容器名
        String sql = "select id,account,password,nickname from t_user ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // 占位符赋值 本次没有占位符,省略

        // 发送查询语句
        ResultSet resultSet = preparedStatement.executeQuery();

        // 创建一个集合, 每个数组存储一个Map对象
        List<Map> mapList = new ArrayList<>();

        // 获取列信息对象
        ResultSetMetaData metaData = resultSet.getMetaData(); //列的信息
        int columnCount = metaData.getColumnCount();//多少列
        while (resultSet.next()) {
            HashMap map = new HashMap();
            for (int i = 1; i <= columnCount; i++) {
                //getColumnLabel获取列名
                map.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            mapList.add(map);
        }

        System.out.println(mapList);

        // 关闭资源close
        preparedStatement.close();
        connection.close();
        resultSet.close();
    }

}
