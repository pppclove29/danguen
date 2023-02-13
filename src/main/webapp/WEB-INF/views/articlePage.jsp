<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 2023-02-12
  Time: 오후 4:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <title>$Article$</title>
</head>
<body>

<pre>
중고물품
    <% ResponseArticleDto article = ((ResponseArticleDto) request.getAttribute("article"));%>

    이미지 -
    <c:forEach var="url" items="${article.imageUrl}">
        <img src="${url}" width="100" height="100"/>
        <h3>${url}</h3>
    </c:forEach>>

    제목 : <%=article.getTitle()%>
    가격 : <%=article.getPrice()%>
    내용 : <%=article.getContent()%>
    판매자 : <%=article.getSeller()%>
    거래희망주소 : <%=article.getDealHopeAddress().toString()%>
    조회수 : <%=article.getViews()%>
</pre>

</body>

<script src="http://code.jquery.com/jquery-latest.js"></script>
<script>
</script>
</html>
