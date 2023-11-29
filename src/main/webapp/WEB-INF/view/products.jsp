<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Product" %>
    <title>Product</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Product </h1>
<br/>
<%--@elvariable id="upload" type="com.example.royalty.dao.BulkUploadDAO"--%>
<from:form method="post" action="/product/upload" enctype="multipart/form-data" modelAttribute="upload">
    <form:input type="file" accept=".csv, .xlsx, .xls" path="file" required="true"/>
    <form:errors path="file" cssClass="text-danger"/>
    <button type="submit">Upload</button>
</from:form>

<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Name</th>
            <th>Code</th>
            <th>Capacity</th>
            <th>Description</th>
            <th>Points</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
           List<Product> products = (List<Product>) request.getAttribute("products");

            for (Product product : products) {
        %>
        <tr>
            <td><a href="/product/<%= product.getId()%>"><%= product.getName() %></a></td>
            <td><%= product.getCode() %></td>
            <td><%= product.getCapacity() %></td>
            <td><%= product.getDescription() %></td>
            <td><%= product.getPoints() %></td>
        </tr>
        <%
            }
        %>
    </tbody>
</table>

<!-- Include necessary JS files -->
<script src="${pageContext.request.contextPath}/js/jquery-3.7.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dataTables.buttons.min.js"></script>
<script src="${pageContext.request.contextPath}/js/buttons.html5.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jszip.min.js"></script>

<script>
    $(document).ready( function () {
        // Initialize DataTable
        var table = $('#dataTable').DataTable( {
            dom: 'Bfrtip',
            buttons: [
                 'csv', 'excel'
            ]
        } );
    });
</script>

</body>
</html>
