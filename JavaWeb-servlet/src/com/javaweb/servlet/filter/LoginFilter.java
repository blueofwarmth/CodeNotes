package com.javaweb.servlet.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //入口验证, session不新建
        HttpSession session = request.getSession(false);

        //在登录请求...或者已经登录的状态下才能访问内部方法
        String servletPath = request.getServletPath();
        if ((session != null && session.getAttribute("username") != null) ||"/user/login".equals(servletPath) ||
                "/index.jsp".equals(servletPath) || "/welcome".equals(servletPath)) {
                //继续往下走
                filterChain.doFilter(request, response);
        } else {
//            response.sendRedirect(request.getContextPath());
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
