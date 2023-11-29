<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Code" %>
    <title>Product</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>Product Codes</h1>
<br/>
<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Codes</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
           List<Code> codes = (List<Code>) request.getAttribute("codes");

            for (Code code : codes) {
        %>
        <tr>
            <td><%= code.getCode() %></td>
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
