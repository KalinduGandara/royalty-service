<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add User</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Add User</h1>
<div class="w-50 p-3">

    <%--@elvariable id="user" type="com.example.royalty.modal.User"--%>
    <form:form action="/user/create" method="post" modelAttribute="user">
    <div class="form-group">
        <label for="name">Name:</label>
        <form:input path="name" type="text" id="name" name="name" class="form-control"/>
        <form:errors path="name" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="nic">NIC:</label>
        <form:input path="nic" type="text" id="nic" name="nic" class="form-control"/>
        <form:errors path="nic" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="nic">EID:</label>
        <form:input path="eid" type="text" id="eid" name="eid" class="form-control"/>
        <form:errors path="eid" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="nic">Password:</label>
        <form:input path="password" type="password" id="password" name="password" class="form-control"/>
        <form:errors path="password" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="description">Description:</label>
        <form:textarea path="description" id="description" name="description" class="form-control"></form:textarea>
    </div>
    <div class="form-group">
        <label for="roll">Roll:</label>
        <form:select path="roll" id="roll" name="roll" class="form-control">
            <form:option value="" label="--Please Select--"/>
            <form:option value="0">Admin</form:option>
            <form:option value="1">User</form:option>
        </form:select>
        <form:errors path="roll" cssClass="text-danger"/>
    </div>
    <button type="submit" class="btn btn-primary">Add User</button>
</div>
</form:form>
<%@include file="footer.jsp" %>
</body>
</html>
