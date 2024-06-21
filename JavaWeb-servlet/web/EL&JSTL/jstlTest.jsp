<%@ page import="java.util.List" %>
<%@ page import="com.javaweb.jdbc.javabean.UserLogin" %>
<%@ page import="java.util.ArrayList" %>
<%--
  Created by IntelliJ IDEA.
  User: Qyingli
  Date: 2024/2/25
  Time: 20:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%--引入核心标签库, 不是网址--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--格式化标签库，专门负责格式化操作的。--%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%--sql标签库--%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%
    // 创建List集合
    List<UserLogin> stuList = new ArrayList<>();

    // 创建Student对象
    UserLogin s1 = new UserLogin("110", "经常");
    UserLogin s2 = new UserLogin("120", "救护车");
    UserLogin s3 = new UserLogin("119", "消防车");

    // 添加到List集合中
    stuList.add(s1);
    stuList.add(s2);
    stuList.add(s3);

    // 将list集合存储到request域当中
    request.setAttribute("stuList", stuList);
%>

<%--需求：将List集合中的元素遍历。输出学生信息到浏览器--%>
<%--使用java代码--%>
<%
    // 从域中获取List集合
    List<UserLogin> stus = (List<UserLogin>)request.getAttribute("stuList");
    // 编写for循环遍历list集合
    for(UserLogin stu : stus){
%>
id:<%=stu.getUsername()%>,name:<%=stu.getUsername()%><br>
<%
    }
%>

<hr>

<%--使用core标签库中forEach标签。对List集合进行遍历--%>
<%--EL表达式只能从域中取数据。--%>
<%--var后面的名字是随意的。var属性代表的是集合中的每一个元素。--%>
<c:forEach items="${stuList}" var="s">
    id:${s.username }, name:${s.password} <br>
</c:forEach>

</body>
</html>
