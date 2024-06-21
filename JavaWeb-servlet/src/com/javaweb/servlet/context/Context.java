package com.javaweb.servlet.context;

import jakarta.servlet.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class Context extends GenericServlet  {
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/html");
        PrintWriter writer = servletResponse.getWriter();

        ServletContext app = this.getServletContext();
        writer.println("context:" + app);

        //获取上下文的初始化信息
        Enumeration<String> initParameterNames = app.getInitParameterNames(); //TODO:快捷键 var
        while(initParameterNames.hasMoreElements()) {
            String name = initParameterNames.nextElement();
            String value = app.getInitParameter(name);
            writer.println("<br>" + name + "|" + value);
        }

        //获取context path (应用上下文的根)
        String contextPath = app.getContextPath();
        writer.println("<br>contextPath:" + contextPath);
        String ContextRealPath = app.getRealPath("index.html");
        writer.println("<br>ContextRealPath:" + ContextRealPath);

        //日志信息
        app.log("全明星制作人们, 大家好!");
    }
}
