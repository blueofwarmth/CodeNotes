<%@ page import="com.javaweb.jdbc.javabean.User" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--毙掉session对象。写上这个，内置对象就不能用了。--%>
<%--<%@page session="false" %>--%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<base href="${pageContext.request.contextPath}/">
		<title>部门列表页面</title>
	</head>
	<body>

	<%--显示一个登录名, 获取session中的用户名--%>
	<h3>欢迎${username.username}</h3>
	<p>当前在线人数:${onlineCount}</p>
	<a href="user/exit">[退出系统:<]</a>


<script type="text/javascript">
	function del(dno){
		var ok = window.confirm("亲，删了不可恢复哦！");
		if(ok){
			document.location.href = "${pageContext.request.contextPath}/user/delete?deptno=" + dno;
		}
	}
</script>

		<h1 align="center">部门列表</h1>
		<hr >
		<table border="1px" align="center" width="50%">
			<tr>
				<th>序号</th>
				<th>部门编号</th>
				<th>部门名称</th>
				<th>操作</th>
			</tr>

			<%
				// 从request域当中取出集合
//				List<User> userList = (List<User>)request.getAttribute("userList");
				// 循环遍历
//				int i = 0;
//				for(User user:userList){
					// 在后台输出
//					System.out.println(user.getDeptname());
					// 把部门名输出到浏览器
	//					out.write(user.getDeptname());
			%>

			<c:forEach items="${userList}" var="user" varStatus="userStatus">
				<tr>
<%--				<td><%=++i%></td>--%>
					<td>${userStatus.count}</td>
					<td>${user.deptno}</td>
					<td>${user.deptname}</td>
					<td>
						<a href="javascript:void(0)" onclick="del(${user.deptno})">删除</a>
							<%--先查询信息回来,再修改返回. flag, 表示时执行详情还是修改, 用于后端detail辨别--%>
						<a href="user/detail?flag=edit&dno=${user.deptno}">修改</a>
						<a href="user/detail?flag=detail&dno=${user.deptno}">详情</a>
					</td>
				</tr>
			</c:forEach>
<%--			<%--%>
<%--				}--%>
<%--			%>--%>
		</table>
		
		<hr >
		<a href="add.jsp">新增部门</a>
	</body>
</html>
