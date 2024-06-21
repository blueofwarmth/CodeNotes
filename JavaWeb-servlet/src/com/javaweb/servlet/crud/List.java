package com.javaweb.servlet.crud;

import com.javaweb.jdbc.javabean.Customer;
import com.javaweb.jdbc.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class List extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        //Connecting SQL
        CustomerService customerService = new CustomerService();
        // getAllEmployee
        java.util.List<Customer> list = customerService.getList();
        if (list == null || list.size() == 0) {
            System.out.println("没有数据, 请添加新数据...");
        } else {
            writer.println("ID\t姓名\t\t性别\t\t年龄\t\t工资\t\t\t电话<br>");
            for (Customer customer : list) {
                writer.println(customer + "<br>");
            }
        }
    }
}
