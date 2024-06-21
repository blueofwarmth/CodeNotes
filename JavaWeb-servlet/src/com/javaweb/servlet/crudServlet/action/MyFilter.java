package com.javaweb.servlet.crudServlet.action;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

//要过滤的路径
@WebFilter("/filter/a")
public class MyFilter implements Filter {
    public MyFilter() {
        System.out.println("MyFilter 无参执行");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 这个方法在过滤器被初始化时调用，可以在这里进行一些初始化操作
        // 例如获取配置信息等
        System.out.println("filterInit");
        System.out.println("init: " + filterConfig.toString());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 这个方法在每次请求被拦截时调用
        // 可以在这里对请求或响应进行处理
        // 这里简单地将请求信息输出到控制台
        System.out.println("Request intercepted!");


        // 调用 filterChain.doFilter() 继续执行过滤器链中的下一个过滤器
        // 如果没有下一个过滤器，则执行目标资源（例如Servlet或JSP）
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // 这个方法在过滤器被销毁时调用
        // 可以在这里进行一些资源释放操作
        System.out.println("Destroy is being called");
    }
}