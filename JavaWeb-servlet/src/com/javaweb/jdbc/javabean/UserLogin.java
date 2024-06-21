package com.javaweb.jdbc.javabean;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

import java.util.Objects;

public class UserLogin implements HttpSessionBindingListener {
    private int id = 0;
    private String username;
    private String password;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        //登录时存放用户数量
        ServletContext application = event.getSession().getServletContext();
        //获取登录人数
        Object onlineCount = application.getAttribute("onlineCount");
        Integer count = 1;
        if(onlineCount == null) {
            application.setAttribute("onlineCount", count);
            System.out.println(application.getAttribute("onlineCount") + " online users");
        } else {
            count = (Integer)onlineCount;
            count++;
            application.setAttribute("onlineCount", count);
            System.out.println("online Users:" + count);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        //用户退出时减少人数
        ServletContext application = event.getSession().getServletContext();

        Object onlineCount = application.getAttribute("onlineCount");
        Integer count = (Integer)onlineCount;
        count--;
        application.setAttribute("onlintCount", count);

    }

    public UserLogin() {
    }
    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
