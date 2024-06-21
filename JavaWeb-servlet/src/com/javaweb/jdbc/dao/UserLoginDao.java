package com.javaweb.jdbc.dao;

import com.javaweb.jdbc.javabean.User;
import com.javaweb.jdbc.javabean.UserLogin;
import com.javaweb.jdbc.utils.BaseDao;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qyingli
 * Description: UserLogin对应的数据库方法
 */
public class UserLoginDao extends BaseDao {
    /**
     * 添加客户的方法
     * @param userLogin
     */
    public void addUser(UserLogin userLogin) throws SQLException {

        String sql = "insert into user_login(username, password) values(?,?);";

        executeUpdate(sql, userLogin.getUsername(), userLogin.getPassword());
    }
    /**
     * 根据用户名和密码验证登录
     * @param deptno
     * @return
     */
    public UserLogin findUserLogin(UserLogin userLogin) throws SQLException, NoSuchFieldException,
            InstantiationException,
            IllegalAccessException {

        String sql = "select * from user_login where username = ? and password = ?;";

        List<UserLogin> userList = executeQuery(UserLogin.class, sql, userLogin.getUsername(), userLogin.getPassword());
        //如果有数据
        if (userList != null && userList.size() > 0) {
            return userList.get(0);
        }
        //无数据就返回空
        return null;
    }

}
