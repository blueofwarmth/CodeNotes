package com.javaweb.jdbc.service;

import com.javaweb.jdbc.dao.UserLoginDao;
import com.javaweb.jdbc.javabean.User;
import com.javaweb.jdbc.javabean.UserLogin;

import java.sql.SQLException;
import java.util.List;

/**
 * 这是一个具有管理功能的功能类. 内部数据不允许外部随意修改, 具有更好的封装性.
 */
public class UserLoginService {
    UserLoginDao userLoginDao = new UserLoginDao();

    /**
     * 用途：添加新客户
     * 参数：userLogin指定要添加的客户对象
     */
    public void addUser(UserLogin userLogin) {
        try {
            userLoginDao.addUser(userLogin);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用途：验证登录
     * 参数：userLogin请求登录的用户
     */
    public UserLogin getUserLogin(UserLogin userLogin) {
        try {
            return userLoginDao.findUserLogin(userLogin);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
