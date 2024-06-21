<%@ page import="com.javaweb.jdbc.javabean.User" %>
<%@page contentType="text/html;charset=UTF-8"%>

<%
	// 从request域当中取出数据
//	User u = (User) request.getAttribute("user"); //使用EL下不需要这句

%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<base href="${pageContext.request.contextPath}/">
		<title>部门详情</title>
	</head>
	<body>
<%--	<h3>欢迎<%=session.getAttribute("username")%></h3>--%>
		<h3>欢迎${username}</h3>
		<h1>部门详情</h1>
		<hr >
<%--		部门编号：<%=u.getDeptno()%> <br>--%>
<%--		部门名称：<%=u.getDeptname()%><br>--%>
<%--		部门位置：<%=u.getDeptloc()%><br>--%>
			部门编号:${user.deptno}<br>
            部门名称:${user.deptname}<br>
            部门位置:${user.deptloc}<br>
		
		<input type="button" value="后退" onclick="window.history.back()"/>
	</body>
</html>
