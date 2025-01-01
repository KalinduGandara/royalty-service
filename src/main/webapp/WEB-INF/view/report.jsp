<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Report</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
          <meta charset="UTF-8">
</head>
<body>
<%@include file="nav.jsp" %>
<div class="m-2 w-auto">
    <h1> Report generate </h1>
    <br/>
<%--@elvariable id="report" type="com.example.royalty.dao.GenerateReportDAO"--%>
<form:form action="/report/master" method="post" modelAttribute="report" cssClass="form-inline">
    <div class="input-group mb-2 mr-sm-2">
        <div class="input-group-prepend">
            <div class="input-group-text"><label for="startDate">Start Date:</label></div>
        </div>
        <form:input path="startDate" type="date" id="startDate" name="startDate" cssClass="form-control"/>
        <form:errors path="startDate" cssClass="text-danger"/>
    </div>
    <div class="input-group mb-2 mr-sm-2">
        <div class="input-group-prepend">
            <div class="input-group-text"><label for="endDate">End Date:</label></div>
        </div>
        <form:input path="endDate" type="date" id="endDate" name="endDate" cssClass="form-control"/>
        <form:errors path="endDate" cssClass="text-danger"/>
    </div>
    <button type="submit" class="btn btn-primary mb-2">Generate Report</button>

</form:form>

</div>

<!-- Include necessary JS files -->
<%@include file="footer.jsp" %>

</body>
</html>
