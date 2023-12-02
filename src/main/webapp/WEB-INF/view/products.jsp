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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/buttons.bootstrap4.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Product </h1>
<br/>
<%--@elvariable id="upload" type="com.example.royalty.dao.BulkUploadDAO"--%>
<from:form method="post" action="/product/upload" enctype="multipart/form-data" modelAttribute="upload">
    <form:input  id="file" type="file" accept=".csv, .xlsx, .xls" path="file" required="true"/>
    <form:errors path="file" cssClass="text-danger"/>
    <button class="btn btn-primary" type="submit">Upload</button>
</from:form>
<br/>
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
                String description = product.getDescription() == null ? "" : product.getDescription();
        %>
        <tr>
            <td><a href="/product/<%= product.getId()%>"><%= product.getName() %></a></td>
            <td><%= product.getCode() %></td>
            <td><%= product.getCapacity() %></td>
            <td><%= description %></td>
            <td><%= product.getPoints() %></td>
        </tr>
        <%
            }
        %>
    </tbody>
</table>

<!-- Include necessary JS files -->
<%@include file="footer.jsp" %>

<script>
    $(document).ready( function () {
        // Initialize DataTable
        const table = $('#dataTable').DataTable({
            dom: 'Bfrtip',
            buttons: [
                'csv', 'excel'
            ]
        });
    });
</script>

</body>
</html>
