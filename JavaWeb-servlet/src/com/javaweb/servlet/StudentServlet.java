package com.javaweb.servlet;

import com.javaweb.jdbc.javabean.Customer;
import com.javaweb.jdbc.service.CustomerService;
import jakarta.servlet.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class StudentServlet implements Servlet {
    public StudentServlet() {
        System.out.println("无参构造器被调用.");
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("init被调用");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //设置响应类型
        servletResponse.setContentType("text/html");
        PrintWriter writer = servletResponse.getWriter();
        //连接数据库
        CustomerService customerService = new CustomerService();
        // 获取所有员工
        List<Customer> list = customerService.getList();
        if (list == null || list.size() == 0) {
            System.out.println("没有数据, 请添加新数据...");
        } else {
            writer.println("ID\t姓名\t\t性别\t\t年龄\t\t工资\t\t\t电话<br>");
            for (Customer customer : list) {
                writer.println(customer + "<br>");
            }
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
