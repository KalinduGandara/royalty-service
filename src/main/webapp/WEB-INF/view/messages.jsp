<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Message" %>
    <%@ page import="java.time.format.DateTimeFormatter" %>
    <title>Product</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1> Product </h1>
<br/>
<!-- Search inputs outside the table -->
<input type="text" id="search" placeholder="search">
<br/>

<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Message</th>
            <th>Phone</th>
            <th>Create Time</th>
            <th>Send</th>
            <th>Send Time</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<Message> messages = (List<Message>) request.getAttribute("messages");

            for (Message message : messages) {
                String sendTime = message.getSendTime() == null ? "Not Sent" : message.getSendTime().format(formatter);
                String createTime = message.getCreateTime() == null ? "" : message.getCreateTime().format(formatter);
                String send = message.getSend() ? "Yes" : "No";

        %>
        <tr>
            <td><%= message.getMessage() %></td>
            <td><%= message.getPhone() %></td>
            <td><%= createTime %></td>
            <td><%= send %></td>
            <td><%= sendTime %></td>
        </tr>
        <%
            }
        %>
    </tbody>
</table>

<!-- Include necessary JS files -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>

<script>
    $(document).ready( function () {
        // Initialize DataTable
        var table = $('#dataTable').DataTable( {
            dom: 'Bfrtip',
            buttons: [
                 'csv', 'excel'
            ]
        } );
        $('#search').on( 'keyup', function () {
            table.search( this.value ).draw();
        } );
    });
</script>

</body>
</html>
