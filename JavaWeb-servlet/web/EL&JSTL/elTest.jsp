<%--
  Created by IntelliJ IDEA.
  User: Qyingli
  Date: 2024/2/22
  Time: 14:40
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>EL</title>
</head>
<body>
<%--从request域当中获取--%>
<%request.setAttribute("username", "点刀哥"); %>
<%=request.getAttribute("username")%>
<%--使用el表达式--%>
<p>el表达式的输出:</p>
<%--从request域中取出, 输出到浏览器--%>
${username}




</body>
</html>
