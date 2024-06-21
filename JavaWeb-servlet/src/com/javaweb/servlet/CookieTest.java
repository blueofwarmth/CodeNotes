package com.javaweb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/cookie/test")
public class CookieTest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie cookie = new Cookie("username", "john_doe"); //key:username
        // value:john_doe
        resp.setContentType("text/Html");
        //设置声明周期
        cookie.setMaxAge(60 * 60);
        //将Cookie添加到HTTP响应中，以便服务器将其发送给客户端。
        resp.addCookie(cookie);
        //设置Cookie的路径为根路径
        cookie.setPath("/");
        //在处理HTTP请求时，可以从请求中获取客户端发送的Cookie。
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("username")) {
                    String uName = c.getValue();
                    System.out.println("username=" + uName);
                    PrintWriter writer = resp.getWriter();
                    writer.println("username=" + uName);
                    // 使用username进行相应的逻辑处理
                    break;
                }
            }
        }
    }
    }
