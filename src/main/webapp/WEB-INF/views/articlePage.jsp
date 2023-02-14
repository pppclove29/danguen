<%@ page import="com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto" %><%--
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
    <title>$Article$</title>
</head>
<body>

<pre>
중고물품
    <% ResponseArticleDto article = ((ResponseArticleDto) request.getAttribute("article"));%>

    이미지 -
    <c:forEach var="imageUrl" items="${article.imageUrl}">
        <img src="/${imageUrl}" width="100" height="100"/>
    </c:forEach>

    제목 : <%=article.getTitle()%>
    가격 : <%=article.getPrice()%>
    내용 : <%=article.getContent()%>
    판매자 : <%=article.getSeller()%>
    거래희망주소 : <%=article.getDealHopeAddress().toString()%>
    조회수 : <%=article.getViews()%>



    ------------------------------------댓글------------------------------------

    댓글달아볼까요?
    <form id="commentForm">
        <input type="text" name="content" id="content"/>
        <button id="btnSend"></button>
    </form>
</pre>

<c:forEach var="comment" items="${comments}">
    ${comment.writer}
    ${comment.content}
    <form action="/comment/${comment.id}" method="post">
        <input type="hidden" name="_method" value="PUT"/>
        <input type="submit" value="삭제"/>
    </form>
    <form action="/comment/${comment.id}" method="post">
        <input type="hidden" name="_method" value="PUT"/>
        <input type="submit" value="수정"/>
    </form>
</c:forEach>

</body>
<script src="http://code.jquery.com/jquery-latest.js">
    $(document).ready(function() {
        $("#btnSend").on("click", function () {
            var commentData = $("#commentForm").serialize();

            $.ajax({
                url: window.location.pathname + '/comment',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(commentData),
                success: function (data) {
                    console.log(data);
                },
                error: function () {
                    console.assert(true);
                }
            })
        })
    })
</script>

</html>
