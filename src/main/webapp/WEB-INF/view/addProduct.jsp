<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Add Product</h1>
<div class="w-50 p-3">

    <%--@elvariable id="product" type="com.example.royalty.modal.Product"--%>
    <form:form action="/product/create" method="post" modelAttribute="product">
    <div class="form-group">
        <label for="name">Name:</label>
        <form:input path="name" type="text" id="name" name="name" class="form-control" />
        <form:errors path="name" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="code">Code:</label>
        <form:input path="code" type="text" id="code" name="code" class="form-control"/>
        <form:errors path="code" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="capacity">Capacity:</label>
        <form:input path="capacity" type="number" id="capacity" name="capacity" class="form-control"/>
        <form:errors path="capacity" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="description">Description:</label>
        <form:textarea path="description" id="description" name="description" class="form-control"></form:textarea>
    </div>
    <div class="form-group">
        <label for="points">Points:</label>
        <form:input path="points" type="number" id="points" name="points" class="form-control"/>
        <form:errors path="points" cssClass="text-danger"/>
    </div>
    <button type="submit" class="btn btn-primary">Add Product</button>
</div>
</form:form>
<%@include file="footer.jsp" %>
</body>
</html>
