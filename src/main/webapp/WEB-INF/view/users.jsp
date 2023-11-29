<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.User" %>
    <title>User</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1> User </h1>
<br/>
<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Name</th>
            <th>NIC</th>
            <th>EID</th>
            <th>Description</th>
            <th>Roll</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
           List<User> users = (List<User>) request.getAttribute("users");

            for (User user : users) {
        %>
        <tr>
            <td><a href="/user/<%= user.getId()%>"><%= user.getName() %></a></td>
            <td><%= user.getNic() %></td>
            <td><%= user.getEid() %></td>
            <td><%= user.getDescription() %></td>
            <td><%= user.getRoll() %></td>
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
        var table = $('#dataTable').DataTable();

    });
</script>

</body>
</html>
