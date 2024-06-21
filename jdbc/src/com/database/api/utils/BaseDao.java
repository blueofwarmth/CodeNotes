package com.database.api.utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//封装dao数据库重复代码
//todo: 简化DQL和非DQL
public abstract class BaseDao {
    /*简化非DQL语句
    * @param sql 带占位符的sql语句
    * @param params 占位符的值
    * @return 返回执行影响的行数
    */
    public int executeUpdate(String sql, Object... params) throws SQLException {//可变参数
        //获取连接
        Connection connection = JDBCUtilsV2.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //占位符赋值, 可变参数可以当做一个数组
        for (int i = 1; i <= params.length; i++) {
            preparedStatement.setObject(i, params[i-1]);
        }
        //发送sql
        int rows = preparedStatement.executeUpdate();
        preparedStatement.close();
        //是否回收连接, 要看是否开启事务
        if (connection.setAutoCommit()) {
            JDBCUtilsV2.FreeConnection();
        }
        return rows;
    }
    /*
    * DQL 语句的返回类型是???
    * 虽然List<map>可以存储, 但是
    * 1. map没有数据校验机制
    * 2. map不支持反射
    * 数据库数据 -> 实体类
    * 在Java中创建一个和表中的数据列一样的实体类存储数据
    * 表中一行 -> Java中一个实体类对象 -> 多行多个对象
    * 实体类支持反射操作
    * 所以返回类型为List<T>, 一个泛型
    * 1. public <T>(使用泛型) List<T>(返回类型) executeUpdate(Class<T> clazz, String sql, Object... params)
    * 2. User.Class, T = User
    * 3. 使用反射技术赋值
    * */

    /*
    * @param clazz 要接值的实体类结合的模板对象
    * @param sql 要求列名等于实体类的属性名! eg: u_id as uid ->(将u_id映射) uid
    * */
    public <T> List<T> executeQuery(Class<T> clazz, String sql, Object... params) throws SQLException, InstantiationException, IllegalAccessException {
        //获取连接
        Connection conneciton = JDBCUtilsV2.getConnection();
        PreparedStatement preparedStatement = conneciton.prepareStatement(sql);
        //占位符赋值
        if (params == null && params.length != 0) {
            for (int i = 1; i <= params.length; i++) {
                preparedStatement.setObject(i, params[i-1]);
            }
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        List<T> list = new ArrayList<>();

        // 获取列信息对象
        ResultSetMetaData metaData = resultSet.getMetaData(); //列的信息
        int columnCount = metaData.getColumnCount();//多少列
        while (resultSet.next()) {
            //一列数据对应一个T类的对象
            T t = clazz.newInstance(); //调用类的无参构造函数实例化对象
            for (int i = 1; i <= columnCount; i++) {
                //对象的属性值
                Object value = resultSet.getObject(i);
                //用getColumnLabel获取指定列的名称
                String propertyName = metaData.getColumnLabel(i);
                //反射赋值
                Field field =  clazz.getDeclaredField(propertyName);
                field.setAccessible(true);//属性可以设置, 打破private的限制
                field.set(t, value);//要赋值的对象, 值
            list.add(t);
            }
        return list;
        }
        if(connection.getAutoCommit()) {
            //没有事务则关闭
            JDBCUtilsV2.FreeConnection();
        }
    }
}
