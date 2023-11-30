<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Customer" %>
    <title>Customer</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>  Customer  </h1>
<br/>
<%--@elvariable id="upload" type="com.example.royalty.dao.BulkUploadDAO"--%>
<from:form method="post" action="/customer/upload" enctype="multipart/form-data" modelAttribute="upload">
    <form:input type="file" accept=".csv, .xlsx, .xls" path="file" required="true"/>
    <form:errors path="file" cssClass="text-danger"/>
    <button type="submit">Upload</button>
</from:form>

<br/>
<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Name</th>
            <th>NIC</th>
            <th>Phone Number</th>
            <th>Address</th>
            <th>Area</th>
            <th>Available Points</th>
            <th>Notes</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
            List<Customer> customers = (List<Customer>) request.getAttribute("customers");

            for (Customer customer : customers) {
        %>
        <tr>
                 <td><a href="/customer/<%= customer.getId()%>"><%= customer.getName() %></a></td>
                <td><%= customer.getNic() %></td>
                <td><%= customer.getPhone() %></td>
                <td><%= customer.getAddress() %></td>
                <td><%= customer.getArea() %></td>
                <td><%= customer.getPoints() %></td>
                <td><%= customer.getNotes() %></td>
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
