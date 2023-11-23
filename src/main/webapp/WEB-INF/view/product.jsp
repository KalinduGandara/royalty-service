<%@ page import="com.example.royalty.modal.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Product</title>
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>View Product</h1>
<div class="w-50 p-3">

    <%--@elvariable id="product" type="com.example.royalty.modal.Product"--%>
    <form:form action="/product/${product.id}" method="post" modelAttribute="product">
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
    <button type="submit" class="btn btn-primary">Update Product</button>
</div>
</form:form>

</body>
</html>
