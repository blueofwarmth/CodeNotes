package com.javaweb.servlet.genericservlet;

import com.javaweb.servlet.genericservlet.GenericServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

public class LoginServlet extends GenericServlet {
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("处理用户登录...");
        //获取config
        ServletConfig servletConfig = getServletConfig();
        System.out.println("configServlet" + servletConfig);
    }
    //重写无参init
    @Override
    public void init(){
        System.out.println("init被重写");
    }
}