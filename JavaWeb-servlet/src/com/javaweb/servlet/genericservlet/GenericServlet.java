package com.javaweb.servlet.genericservlet;

import jakarta.servlet.*;

import java.io.IOException;
/*
* 通用的servlet接口
* 之后所有类只需要继承即可, 不需要直接实现servlet接口
* */
public abstract class GenericServlet implements Servlet {
    //成员变量
    private ServletConfig servletConfig;
    //保护init, 加上final
    //在创建一个init, 用于对外实现重写
    @Override
    public final void init(ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        this.init();
    }
    public void init() {}

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    /**
     * 抽象方法, 常用的方法子类必须实现
     * @param servletRequest
     * @param servletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public abstract void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
