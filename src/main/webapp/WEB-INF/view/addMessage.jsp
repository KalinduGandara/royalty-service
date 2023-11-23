<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Customer" %>
    <title>Bulk Message</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1> Bulk Message </h1>
<br/>
<input type="text" id="search" placeholder="search">
<%--@elvariable id="message" type="com.example.royalty.dao.BulkMessage"--%>
<form:form action="/message/create" method="post" modelAttribute="message">
<form:input path="message" type="text" placeholder="Mesage"/>
<input type="submit" value="Send Message"/>
<table id="dataTable" class="display">
    <thead>
    <tr>
        <th>Send</th>
        <th>Name</th>
        <th>Phone Number</th>
    </tr>
    </thead>
    <tbody>
    <!-- Here, you can dynamically populate the table rows with data -->
    <%
        List<Customer> customers = (List<Customer>) request.getAttribute("customers");

        for (Customer customer : customers) {
    %>
    <tr>
        <td><form:checkbox value="<%= customer.getId() %>" path="cids"/></td>
        <td><%= customer.getName() %></td>
        <td><%= customer.getPhone() %></td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>
</form:form>

<%--qweqweqwe--%>
<%--<form:form action="/message/create" method="post" modelAttribute="message">--%>
<%--    <form:input path="message" type="text"/>--%>

<%--        <jsp:useBean id="customers" scope="request" type="java.util.List"/>--%>
<%--        <form:checkboxes path="cid" items="${customers}"/>--%>

<%--</form:form>--%>
<!-- Include necessary JS files -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>

<script>
    $(document).ready( function () {
        // Initialize DataTable
        var table = $('#dataTable').DataTable();

        $('#search').on( 'keyup', function () {
            table.search( this.value ).draw();
        } );
    });
</script>

</body>
</html>
