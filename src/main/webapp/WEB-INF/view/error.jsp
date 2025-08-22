<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <meta charset="UTF-8">
</head>
<body>
    <%@include file="nav.jsp" %>
    <%
        String message = (String) request.getAttribute("message");
        if (message == null) {
            message = "An unexpected error occurred.";
        }
        String status = (String) request.getAttribute("status");
    %>

    <div class="alert alert-danger" role="alert">
        <strong>Error:</strong> <%= message %>
        <% if (status != null) { %>
            <br>Status Code: <%= status %>
        <% } %>
        <br>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Home</a>
    </div>
</body>
</html>