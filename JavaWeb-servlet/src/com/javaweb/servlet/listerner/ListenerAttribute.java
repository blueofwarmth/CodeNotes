package com.javaweb.servlet.listerner;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;

//@WebListener
public class ListenerAttribute implements HttpSessionAttributeListener {
//    session域中发生对应的事件时执行
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        System.out.println("Attribute Added");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        System.out.println("Attribute Removed");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        System.out.println("Attribute Replaced");
    }
}
