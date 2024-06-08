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
        <label for="tpNumber">TP Number:</label>
        <form:input path="tpNumber" id="tpNumber" name="tpNumber" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="custName">Customer Name:</label>
        <form:input path="name" id="custName" name="custName" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="address">Address:</label>
        <form:input path="address" id="address" name="address" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="telephoneNo">Telephone No:</label>
        <form:input path="phone" id="telephoneNo" name="telephoneNo" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="city">City:</label>
        <form:input path="city" id="city" name="city" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="district">District:</label>
        <form:input path="district" id="district" name="district" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="province">Province:</label>
        <form:input path="province" id="province" name="province" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="salesPersonTerritory">Sales Person Territory:</label>
        <form:input path="salesPersonTerritory" id="salesPersonTerritory" name="salesPersonTerritory" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="region">Region:</label>
        <form:input path="region" id="region" name="region" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="assignedCMDE">Assigned CMDE:</label>
        <form:input path="assignedCMDE" id="assignedCMDE" name="assignedCMDE" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="loyaltyStatus">Loyalty Status:</label>
        <form:input path="loyaltyStatus" id="loyaltyStatus" name="loyaltyStatus" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="currentAveConsumptionPM">Current Ave consumption P/M:</label>
        <form:input path="currentAveConsumptionPM" id="currentAveConsumptionPM" name="currentAveConsumptionPM" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="linkedDealer1">Linked dealer 1:</label>
        <form:input path="linkedDealer1" id="linkedDealer1" name="linkedDealer1" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="linkedDealer2">Linked dealer 2:</label>
        <form:input path="linkedDealer2" id="linkedDealer2" name="linkedDealer2" class="form-control"></form:input>
    </div>
    <div class="form-group">
        <label for="notes">Notes:</label>
        <form:textarea path="notes" id="notes" name="notes" class="form-control"></form:textarea>
    </div>
    <button type="submit" class="btn btn-primary">Add Customer</button>
</div>
</form:form>
<%@include file="footer.jsp" %>
</body>
</html>
