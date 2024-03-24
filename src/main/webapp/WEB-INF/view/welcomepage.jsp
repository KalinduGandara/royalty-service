<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Royalty</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%--Nav bar--%>
<%@include file="nav.jsp" %>

<%--JSP Content--%>
<div class="container">
    <div class="starter-template">
        <h1>Home page</h1>
        <p class="lead">${message}</p>
    </div>
</div>
<%@include file="footer.jsp" %>
</body>
</html>
