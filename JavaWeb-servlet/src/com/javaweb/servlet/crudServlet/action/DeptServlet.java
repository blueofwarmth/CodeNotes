package com.javaweb.servlet.crudServlet.action;

import com.javaweb.jdbc.javabean.User;
import com.javaweb.jdbc.javabean.UserLogin;
import com.javaweb.jdbc.service.UserLoginService;
import com.javaweb.jdbc.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/user/login", "/user/exit", "/user/list", "/user/detail", "/user/delete", "/user/save", "/user/modify"})
public class DeptServlet extends HttpServlet {
    int countUsers = 0;
    UserService userService = new UserService();
    UserLoginService userLoginService = new UserLoginService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        switch (servletPath) {
            case "/user/list":
                doList(request, response);
                break;
            case "/user/detail":
                doDetail(request, response);
                break;
            case "/user/delete":
                doDel(request, response);
                break;
            case "/user/save":
                doSave(request, response);
                break;
            case "/user/modify":
                doModify(request, response);
                break;
            case "/user/login":
                doLogin(request, response);
                break;
            case "/user/exit":
                doExit(request, response);
                break;
            default:
//                response.sendRedirect(request.getContextPath());
                response.sendRedirect(request.getContextPath() + "index.jsp");
                break;
        }
    }

    /**
     * 保存部门信息
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取部门的信息
        String userno = request.getParameter("deptno");
        int usern = Integer.valueOf(userno).intValue(); //note:String转为int
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");

        // 连接数据库执行insert语句
        User user = new User(usern, dname, loc);
        userService.addUser(user);

        // 重定向到列表页面
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/user/list");
    }

    /**
     * 根据部门编号删除部门
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doDel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取部门编号
        String userno = request.getParameter("deptno");
        int userNo = Integer.parseInt(userno);
        //输出要删除的编号
        System.out.println("删除的部门ID:" + userNo);
        boolean count = false;
        // 连接数据库，删除部门
//					System.out.println(user.getDeptname());
        count = userService.removeUser(userNo);

        if (count) {
            // 删除成功
            // 重定向到列表页面
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/user/list");
        }
    }

    /**
     * 根据部门编号获取部门的信息。
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 创建部门对象
        User user = new User();

        // 获取部门编号
        String dno = request.getParameter("dno");
        int userno = Integer.parseInt(dno);
        System.out.println("部门号" + userno);
        //连接数据库
        user = userService.getUser(userno);
        // 这个豆子只有一个，所以不需要袋子，只需要将这个咖啡豆放到request域当中即可。
        request.setAttribute("user", user);

        // 转发（不是重定向，因为要跳转到JSP做数据展示）
        //request.getRequestDispatcher("/detail.jsp").forward(request, response);

        /*String f = request.getParameter("f");
        if ("m".equals(f)) {
            // 转发到修改页面
            request.getRequestDispatcher("/edit.jsp").forward(request, response);
        } else if("d".equals(f)){
            // 转发到详情页面
            request.getRequestDispatcher("/detail.jsp").forward(request, response);
        }*/
        //通过前端的flag辨别接下来转发详情还是修改
        request.getRequestDispatcher("/" + request.getParameter("flag") + ".jsp").forward(request, response);

    }

    /**
     * 连接数据库，查询所有的部门信息，将部门信息收集好，然后跳转到JSP做页面展示。
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 准备一个容器，用来专门存储部门
        // 连接数据库，查询所有的部门信息
        List<User> user = userService.getList();
        // 将一个集合放到请求域当中
        request.setAttribute("userList", user);

        // 转发（不要重定向）
        //如果在上一个Servlet当中向request域当中绑定了数据，希望从下一个Servlet当中把request域里面的数据取出来，使用转发机制.
        request.getRequestDispatcher("/list.jsp").forward(request, response);

    }

    /**
     * 修改部门
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doModify(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 解决请求体的中文乱码问题。
        request.setCharacterEncoding("UTF-8");

        // 获取表单中的数据
        String userno = request.getParameter("deptno");
        int usern = Integer.parseInt(userno);
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");
        boolean count = false;
        // 连接数据库执行更新语句
        User user = new User(usern, dname, loc);
        count = userService.modifyUser(usern, user);

        if (count) {
            response.sendRedirect(request.getContextPath() + "/user/list");
        }
    }

    /**
     * 新增登录用户账号和密码
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //获取用户名和密码 username=?, password=?
        String uName = request.getParameter("username");
        String uPassword = request.getParameter("password");
        UserLogin userLogin = new UserLogin(uName, uPassword);
        //连接数据库存储
        UserLogin userLogins = userLoginService.getUserLogin(userLogin);
        System.out.println(userLogins);
        //返回登录结果
        if (userLogins != null) {
            //获取session对象
            HttpSession session = request.getSession();
            //存入用户
            session.setAttribute("username", userLogin);
            //设置保存登录信息时间
            String flagLogin = request.getParameter("flag_login");
            //设置cookie
            if (flagLogin != null && flagLogin.equals("1")) {
                //创建cookie
                Cookie userCookie = new Cookie("loginName", uName);
                Cookie passwordCookie = new Cookie("loginPassword", uPassword);
                userCookie.setMaxAge(60 * 60 * 24 * 7); //保存七天
                passwordCookie.setMaxAge(60 * 60 * 24 * 7); //保存七天
                //设置路径 只要访问这个应用, 就一定携带cookie数据
                userCookie.setPath(request.getContextPath());
                passwordCookie.setPath("/");
                //添加cookie
                response.addCookie(userCookie);
                response.addCookie(passwordCookie);
            }
            //重定向到list页面
            response.sendRedirect(request.getContextPath() + "/user/list");
        } else {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    /**
     * 用于安全退出
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void doExit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //获取session对象, 并销毁
        HttpSession session = request.getSession();
        if (session != null) {
            //从session域中移除
            session.removeAttribute("username");
            session.invalidate();

            Cookie[] cookies = request.getCookies();
            //退出时销毁cookie
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    if (name.equals("username") || name.equals("password")) {
                        cookie.setMaxAge(0);
                        cookie.setPath(request.getContextPath()); //项目下所有cookie都删除
                        response.addCookie(cookie); //结果响应
                    }
                }
            }
            //回到根目录
            response.sendRedirect(request.getContextPath());

        }
    }
}




















