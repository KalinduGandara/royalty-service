<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>

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
<div class="container m-2 w-auto">
    <h1> Bulk Message </h1>
    <br/>
    <%--@elvariable id="message" type="com.example.royalty.dao.BulkMessageDAO"--%>
    <form:form action="/message/create" method="post" modelAttribute="message">
        <form:textarea path="message" type="text" placeholder="Mesage" rows="5" cols="50" class="form-control"/>
        <form:errors path="message" cssClass="text-danger"/>
        <from:errors path="cids" cssClass="text-danger"/>
        <input type="submit" value="Send Message" class="btn btn-primary my-2"/>
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
</div>

<!-- Include necessary JS files -->
<%@include file="footer.jsp" %>

<script>
    $(document).ready( function () {
        // Initialize DataTable
        const table = $('#dataTable').DataTable();

        $('#search').on( 'keyup', function () {
            table.search( this.value ).draw();
        } );
    });
</script>

</body>
</html>
