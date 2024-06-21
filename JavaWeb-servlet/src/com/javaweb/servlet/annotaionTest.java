package com.javaweb.servlet;


import com.javaweb.jdbc.javabean.Customer;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="serlvet",//相当于<servlet-name>
        urlPatterns = {"/servlet/annotations"},//<url-pattern>
//        value = {"/servlet/annotations"},//和url-patterns一样, 都是指定servlet映射路径, 为数组类型, 用{}
        //loadOnStartup=1,//<load-on-startup> 启动时是否创建servlet对象
        initParams = {@WebInitParam(name="username", value="root"), @WebInitParam(name="password", value="root")}
)
public class annotaionTest extends HttpServlet {
    public annotaionTest() {
        System.out.println("无参方法执行.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        //获取servletName
        String servletName = getServletName();
        String url = req.getServletPath();
        writer.println("ServletName:" + servletName);
        writer.println("url-pattern:" + url);

    }

}
