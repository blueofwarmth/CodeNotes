package com.javaweb.httpservlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet  extends HttpServlet {
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("处理用户登录...");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        resp.setContentType("text/Html");
        PrintWriter writer = resp.getWriter();
        writer.println(req);

        //HttpServletRequest常用方法
        //获取IP地址
        String ip = req.getRemoteAddr();
        System.out.println("客户端IP地址:" + ip);
        //获取请求的URI
        String uri = req.getRequestURI();
        System.out.println(uri);
        //获取请求的路径
        String path = req.getPathInfo();
        System.out.println(path);
        //获取请求的QueryString
        String queryString = req.getQueryString();
        System.out.println(queryString);
        //获取请求的Http版本
        String httpVersion = req.getProtocol();
        System.out.println(httpVersion);
        //获取请求的编码
        String encoding = req.getCharacterEncoding();
        System.out.println(encoding);
        //获取请求的Content-Type
        String contentType = req.getContentType();
        System.out.println(contentType);
        //获取请求的Content-Length
        int contentLength = req.getContentLength();
        System.out.println(contentLength);
    }
}
