<%@ page import="com.example.royalty.modal.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>View User</title>
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>View User</h1>
<div class="w-50 p-3">

    <%--@elvariable id="user" type="com.example.royalty.modal.User"--%>
    <form:form action="/user/${user.id}" method="post" modelAttribute="user">
    <div class="form-group">
        <label for="name">Name:</label>
        <form:input path="name" type="text" id="name" name="name" class="form-control" />
        <form:errors path="name" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="nic">NIC:</label>
        <form:input path="nic" type="text" id="nic" name="nic" class="form-control"/>
        <form:errors path="nic" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="description">Description:</label>
        <form:textarea path="description" id="description" name="description" class="form-control"></form:textarea>
    </div>
    <div class="form-group">
        <label for="roll">Roll:</label>
        <form:input path="roll" type="text" id="roll" name="roll" class="form-control"/>
        <form:errors path="roll" cssClass="text-danger"/>
    </div>
    <button type="submit" class="btn btn-primary">Update User</button>
</div>
</form:form>

</body>
</html>
