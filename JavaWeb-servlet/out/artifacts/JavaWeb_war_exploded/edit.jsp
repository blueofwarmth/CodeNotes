<%@ page import="com.javaweb.jdbc.javabean.User" %>
<%@page contentType="text/html;charset=UTF-8"%>

<%
//	后端返回的用户为"user"
//	User u = (User)request.getAttribute("user");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<base href="${pageContext.request.contextPath}/">
		<title>修改部门</title>
	</head>
	<body>
<%--	<h3>欢迎<%=session.getAttribute("username")%></h3>--%>
	<h3>欢迎${username}</h3>
		<h1>修改部门</h1>
		<hr >
		<form action="user/modify" method="post">
			部门编号<input type="text" name="deptno" value="${user.deptno}" placeholderreadonly /><br>
			部门名称<input type="text" name="dname" value="${user.deptname}"/><br>
			部门位置<input type="text" name="loc" value="${user.deptloc}"/><br>
			<input type="submit" value="修改"/><br>
		</form>
	</body>
</html>
