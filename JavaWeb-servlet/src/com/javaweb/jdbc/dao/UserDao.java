package com.javaweb.jdbc.dao;

import com.javaweb.jdbc.javabean.User;
import com.javaweb.jdbc.utils.BaseDao;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author Qyingli
 * Description: User对应的数据库方法
 */
public class UserDao extends BaseDao {



    /**
     * 查询数据库客户集合
     * @return
     */
    public List<User> findAll() throws SQLException, NoSuchFieldException, InstantiationException,
            IllegalAccessException {

        List<User> userList = executeQuery(User.class, "select * from dept");

        return userList;
    }

    /**
     * 添加客户的方法
     * @param user
     */
    public void addUser(User user) throws SQLException {

        String sql = "insert into dept(deptno,deptname,deptloc) values(?,?,?);";

        executeUpdate(sql, user.getDeptno(), user.getDeptname(), user.getDeptloc());
    }



    /**
     * 修改对象信息
     * @param us
     * @return 影响行数
     */
    public  int updateById(User us) throws SQLException {

        String sql = "update dept set deptno=?,deptname=?,deptloc=? where deptno = ? ;";

        int rows = executeUpdate(sql, us.getDeptno(), us.getDeptname(), us.getDeptloc(), us.getDeptno());
        return rows;
    }

    /**
     * 根据id查询客户信息
     * @param deptno
     * @return
     */
    public User findById(int deptno) throws SQLException, NoSuchFieldException, InstantiationException,
            IllegalAccessException {

        String sql = "select * from dept where deptno = ?;";

        List<User> userList = executeQuery(User.class, sql, deptno);
        //如果有数据
        if (userList != null && userList.size() > 0) {
            return userList.get(0);
        }
        //无数据就返回空
        return null;
    }

    public int removeById(int deptno) throws SQLException {

        String sql = "delete from dept where deptno = ? ;";

        int rows = executeUpdate(sql, deptno);

        return rows;
    }
}
