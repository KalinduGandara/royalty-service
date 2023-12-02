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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/buttons.bootstrap4.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>  Customer  </h1>
<br/>
<%--@elvariable id="upload" type="com.example.royalty.dao.BulkUploadDAO"--%>
<from:form method="post" action="/customer/upload" enctype="multipart/form-data" modelAttribute="upload">
    <form:input  id="file" type="file" accept=".csv, .xlsx, .xls" path="file" required="true"/>
    <form:errors path="file" cssClass="text-danger"/>
    <button class="btn btn-primary" type="submit">Upload</button>
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
                String address = customer.getAddress() == null ? "" : customer.getAddress();
                String area = customer.getArea() == null ? "" : customer.getArea();
                String notes = customer.getNotes() == null ? "" : customer.getNotes();
        %>
        <tr>
                 <td><a href="/customer/<%= customer.getId()%>"><%= customer.getName() %></a></td>
                <td><%= customer.getNic() %></td>
                <td><%= customer.getPhone() %></td>
                <td><%= address %></td>
                <td><%= area %></td>
                <td><%= customer.getPoints() %></td>
                <td><%= notes %></td>
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
