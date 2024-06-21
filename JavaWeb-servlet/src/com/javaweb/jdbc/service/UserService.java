package com.javaweb.jdbc.service;

import com.javaweb.jdbc.dao.UserDao;
import com.javaweb.jdbc.dao.UserDao;
import com.javaweb.jdbc.javabean.User;

import java.sql.SQLException;
import java.util.List;

/**
 * 这是一个具有管理功能的功能类. 内部数据不允许外部随意修改, 具有更好的封装性.
 */
public class UserService {


    private UserDao userDao = new UserDao();

    /**
     * 用途：返回所有客户对象
     * 返回：集合
     */
    public List<User> getList() {

        try {
            return userDao.findAll();
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


    /**
     * 用途：添加新客户
     * 参数：user指定要添加的客户对象
     */
    public void addUser(User user) {

        try {
            userDao.addUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用途：返回指定id的客户对象记录
     * 参数： id 就是要获取的客户的id号.
     * 返回：封装了客户信息的User对象
     */
    public User getUser(int id) {

        try {
            return userDao.findById(id);
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

    /**
     * 修改指定id号的客户对象的信息
     * @param deptno 部门id
     * @param us 对象
     * @return 修改成功返回true, false表明指定id的客户未找到
     */
    public boolean modifyUser(int deptno, User us) {

        int rows = 0;
        try {
            rows = userDao.updateById(us);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //返回修改成功
        if (rows == 0){
            return false;
        }

        return true;
    }

    /**
     * 用途：删除指定id号的的部门对象记录
     * 参数： id 要删除的部门的id号
     * 返回：删除成功返回true；false表示没有找到
     */
    public boolean removeUser(int deptno) {
        int rows = 0;
        try {
//					System.out.println(user.getDeptname());
            rows = userDao.removeById(deptno);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (rows == 0){
            return false;
        }
        return true;
    }

}
