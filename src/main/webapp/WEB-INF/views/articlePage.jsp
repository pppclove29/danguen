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
    <title>$Article_List$</title>
</head>
<body>

<pre>
중고물품

    제목 : <%=((ResponseArticleDto)request.getAttribute("article")).getTitle()%>
    가격 : <%=((ResponseArticleDto)request.getAttribute("article")).getPrice()%>
    내용 : <%=((ResponseArticleDto)request.getAttribute("article")).getContent()%>
    판매자 : <%=((ResponseArticleDto)request.getAttribute("article")).getSeller()%>
    거래희망주소 : <%=((ResponseArticleDto)request.getAttribute("article")).getDealHopeAddress().toString()%>
    조회수 : <%=((ResponseArticleDto)request.getAttribute("article")).getViews()%>
</pre>

</body>

<script src = "http://code.jquery.com/jquery-latest.js"></script>
<script>
</script>
</html>
