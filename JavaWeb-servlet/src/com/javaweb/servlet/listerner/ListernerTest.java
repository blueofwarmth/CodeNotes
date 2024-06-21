package com.javaweb.servlet.listerner;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ListernerTest implements ServletContextListener {
    @Override
    //上下文初始化
    //服务器启动时
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("contextInitialized");
    }

    //服务器关闭时
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
        System.out.println("contextDestroyed is being destroyed");
    }
}
