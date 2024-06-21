<%@ page import="java.util.ArrayList" %>
<%@ page import="com.javaweb.jdbc.javabean.UserLogin" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Qyingli
  Date: 2024/2/25
  Time: 21:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%--引入核心标签库--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--核心标签库中的if标签--%>
<%--test属性是必须的。test属性支持EL表达式。test属性值只能是boolean类型。--%>
<c:if test="${empty param.username}">
    <p><strong>用户名不能为空。</strong></p>
</c:if>

<%--没有else标签的话，可以搞两个if出来。--%>
<%--if标签还有var属性，不是必须的。--%>
<%--if标签还有scope属性，用来指定var的存储域。也不是必须的。--%>
<%--scope有四个值可选：page(pageContext域)、request(request域)、session(session域)、application(application域)--%>
<%--将var中的v存储到request域。--%>
<c:if test="${not empty param.username}" var="v" scope="request">
<%--浏览器地址栏输入username=xx即可进入--%>
<%--通过param获取到请求域当中的username属性--%>
    <h1>欢迎你${param.username}。</h1>
</c:if>
<p>结果:</p>
<%--通过EL表达式将request域当中的v取出--%>
<%--v变量中存储的是test属性的值。--%>
${v}
<hr>

<%--var用来指定循环中的变量--%>
<%--begin开始--%>
<%--end结束--%>
<%--step步长--%>
<%--底层实际上：会将i存储到pageContext域当中。--%>
<c:forEach var="i" begin="1" end="10" step="1">
    <%--所以这里才会使用EL表达式将其取出，一定是从某个域当中取出的。--%>
    ${i}
</c:forEach>

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
<p>结果:</p>
<%--var="s"这个s代表的是集合中的每个Student对象--%>
<%--varStatus="这个属性表示var的状态对象，这里是一个java对象，这个java对象代表了var的状态"--%>
<%--varStatus="这个名字是随意的"--%>
<%--varStatus这个状态对象有count属性。可以直接使用。--%>
<c:forEach items="${stuList}" var="s" varStatus="stuStatus">
    <%--varStatus的count值从1开始，以1递增，主要是用于编号/序号。--%>
    编号：${stuStatus.count},
    id:${s.username },
    name:${s.password } <br>
</c:forEach>
<hr>
<%--
    if(){

    }else if(){

    }else if(){

    }else if(){

    }else{

    }
--%>

<%--注意：这个嵌套结构不能随便改，只能这样嵌套。--%>
<c:choose>
    <c:when test="${param.age < 18}">
        青少年
    </c:when>
    <c:when test="${param.age < 35}">
        青年
    </c:when>
    <c:when test="${param.age < 55}">
        中年
    </c:when>
    <c:otherwise>
        老年
    </c:otherwise>
</c:choose>

</body>
</html>
