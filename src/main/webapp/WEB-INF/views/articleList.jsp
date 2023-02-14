<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 2023-02-12
  Time: 오후 4:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>$Article_List$</title>
</head>
<body>
<c:forEach var="article" items="${articles}">
    <li>
        <div>
            <img src="${article.imageUrl}" width="200" height="200">
            <a href="/article/${article.id}">${article}</a>
        </div>
    </li>
</c:forEach>

</body>

<script src="http://code.jquery.com/jquery-latest.js">

</script>
</html>
