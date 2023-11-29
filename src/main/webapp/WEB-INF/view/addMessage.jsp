<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Customer" %>
    <title>Bulk Message</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1> Bulk Message </h1>
<br/>
<%--@elvariable id="message" type="com.example.royalty.dao.BulkMessageDAO"--%>
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
<script src="${pageContext.request.contextPath}/js/jquery-3.7.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.dataTables.min"></script>
<script src="${pageContext.request.contextPath}/js/dataTables.buttons.min"></script>
<script src="${pageContext.request.contextPath}/js/buttons.html5.min"></script>
<script src="${pageContext.request.contextPath}/js/jszip.min"></script>

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
