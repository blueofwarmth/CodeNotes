package com.javaweb.servlet.listerner;

import com.javaweb.jdbc.javabean.UserLogin;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/servlet/binding")
public class BindingTest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserLogin userLogin = new UserLogin("11", "22");
        HttpSession session = req.getSession();
        session.setAttribute("user", userLogin);
    }
}
