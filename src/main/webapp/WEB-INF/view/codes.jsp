<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Code" %>
    <%@ page import="java.time.format.DateTimeFormatter" %>
    <title>View Codes</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
          <meta charset="UTF-8">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Product Codes</h1>
<br/>
<form method="get" class="form-inline">
    <div class="input-group mb-2 mr-sm-2">
        <div class="input-group-prepend">
            <div class="input-group-text"><label for="startDate">Start Date:</label></div>
        </div>
        <input class="form-control" type="date" id="startDate" name="startDate" value="${startDate}">
    </div>
    <div class="input-group mb-2 mr-sm-2">
        <div class="input-group-prepend">
            <div class="input-group-text"><label for="endDate">End Date:</label></div>
        </div>
        <input class="form-control" type="date" id="endDate" name="endDate" value="${endDate}">
    </div>
    <button type="submit" class="btn btn-primary mb-2">View Codes</button>
</form>
<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Codes</th>
            <th>Created At</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<Code> codes = (List<Code>) request.getAttribute("codes");
            for (Code code : codes) {
                String createdAt = code.getCreatedAt().format(formatter);
        %>
        <tr>
            <td><%= code.getCode() %></td>
            <td><%= createdAt %></td>
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
