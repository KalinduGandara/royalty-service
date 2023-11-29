<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Add Customer</h1>
<div class="w-50 p-3">

    <%--@elvariable id="customer" type="com.example.royalty.modal.Customer"--%>
    <form:form action="/customer/create" method="post" modelAttribute="customer">
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
        <label for="phone">Phone:</label>
        <form:input path="phone" type="text" id="phone" name="phone" class="form-control"/>
        <form:errors path="phone" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="address">Address:</label>
        <form:textarea path="address" id="address" name="address" class="form-control"></form:textarea>
    </div>
    <div class="form-group">
        <label for="area">Area:</label>
        <form:input path="area" type="text" id="area" name="area" class="form-control"/>
        <form:errors path="area" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="points">Points:</label>
        <form:input path="points" type="number" id="points" name="points" class="form-control"/>
        <form:errors path="points" cssClass="text-danger"/>
    </div>
    <div class="form-group">
        <label for="notes">Notes:</label>
        <form:textarea path="notes" id="notes" name="notes" class="form-control"></form:textarea>
    </div>
    <button type="submit" class="btn btn-primary">Add Customer</button>
</div>
</form:form>

</body>
</html>
