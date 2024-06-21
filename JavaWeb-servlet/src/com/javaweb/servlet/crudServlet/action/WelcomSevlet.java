package com.javaweb.servlet.crudServlet.action;

import com.javaweb.jdbc.javabean.UserLogin;
import com.javaweb.jdbc.service.UserLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 控制登录, 当前访问的用户是否已经登陆过并选择免登录
 */
@WebServlet("/welcome") //默认的访问页面
public class WelcomSevlet extends HttpServlet{
    UserLoginService userLoginService = new UserLoginService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取cookie
        Cookie[] cookies = req.getCookies();
        String loginName = null;
        String loginPassword = null;
        if(cookies != null) {
            for(Cookie c : cookies) {
                if(c.getName().equals("loginName")) {
                    loginName = c.getValue();
//                    System.out.println(loginName);
                }
                if(c.getName().equals("loginPassword")) {
                    loginPassword = c.getValue();
//                    System.out.println(loginPassword);
                }
            }
        }
        //使用获取到的cookie
        if (loginName != null && loginPassword != null) {
            //验证密码和用户名
            UserLogin userLogin = new UserLogin(loginName, loginPassword);
            //连接数据库存储
            UserLogin userLogins = userLoginService.getUserLogin(userLogin);
            //验证成功与否
            if(userLogins != null) {
                resp.sendRedirect(req.getContextPath() + "/user/list");
            } else {
                resp.sendRedirect(req.getContextPath() + "index.jsp");
//                resp.sendRedirect(req.getContextPath());
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }
}
