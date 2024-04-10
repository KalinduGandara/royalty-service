<%@ page import="com.example.royalty.modal.Customer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Customer</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-6 p-3">
            <h1>View Customer</h1>
            <div class="w-50 p-3">
                <%--@elvariable id="customer" type="com.example.royalty.modal.Customer"--%>
                <form:form action="/customer/${customer.id}" method="post" modelAttribute="customer">
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
                        <input disabled value="${customer.points}" type="number" id="points" name="points"
                               class="form-control"/>
                        <form:hidden path="points"/>
                        <form:errors path="points" cssClass="text-danger"/>
                    </div>
                    <div class="form-group">
                        <label for="notes">Notes:</label>
                        <form:textarea path="notes" id="notes" name="notes" class="form-control"></form:textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Update Customer</button>
                </form:form>
            </div>
        </div>
        <div class="col-md-6 p-3">
            <h2>Redeem Points</h2>
            <%--@elvariable id="redeemPoints" type="com.example.royalty.modal.RedeemCustomerPoints"--%>
            <form:form method="post" action="/customer/${customer.id}/redeem"  modelAttribute="redeemPoints" class="form-inline">
                <div class="input-group mb-2 mr-sm-2">
                    <div class="input-group-prepend">
                        <div class="input-group-text"><label for="redeemPoints">Points:</label></div>
                    </div>
                    <form:input path="redeemPoints" type="number" id="redeemPoints" name="rowCount" class="form-control"/>
                    <form:errors path="redeemPoints" cssClass="text-danger"/>
                </div>
                <button class="btn btn-primary mb-2" type="submit">Redeem Points</button>
            </form:form>
        </div>
    </div>
</div>
        <%@include file="footer.jsp" %>
</body>
</html>
