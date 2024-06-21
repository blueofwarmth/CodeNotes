package com.javaweb.servlet.config;

import jakarta.servlet.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * 了解servletConfig
 * - 什么是ServletConfig？
 * - Servlet对象的配置信息对象。
 * - ServletConfig对象中封装了<servlet></servlet>标签中的配置信息。（web.xml文件中servlet的配置信息）
 * - 一个Servlet对应一个ServletConfig对象。
 * - Servlet对象是Tomcat服务器创建，并且ServletConfig对象也是Tomcat服务器创建。并且默认情况下，他们都是在用户发送第一次请求的时候创建。
 * - Tomcat服务器调用Servlet对象的init方法的时候需要传一个ServletConfig对象的参数给init方法。
 * - ServletConfig接口的实现类是Tomcat服务器给实现的。（Tomcat服务器说的就是WEB服务器。）
 */
public class TestServlet extends GenericServlet {

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/html");
        PrintWriter writer = servletResponse.getWriter();

        //获取ServetConfig对象, 实现了servletConfig接口
        ServletConfig config = this.getServletConfig();
        writer.println("ServletConfig对象: " + config.toString());

        //获取<servlet-name>
        String servletName = this.getServletName(); //当前对象TestServlet
        //写html代码
        writer.println("<br><servlet-name>" + servletName + "</servlet-name>");
        //获取初始化信息
        Enumeration<String> initParameterNames = this.getInitParameterNames();
        while(initParameterNames.hasMoreElements()) {
            String initParameterName = initParameterNames.nextElement();
            //通过name获取value
            String initParameterValue = this.getInitParameter(initParameterName);
            writer.println(initParameterName + ":" + initParameterValue + "</br>");
        }

        //通过servletConfig或者this获取servletContext对象
//        ServletContext app = config.getServletContext();
        ServletContext app = this.getServletContext();
        writer.print("<br>" + app);
    }

}
