<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Customer" %>
    <title>Customer</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
</head>
<body>
<h1> <center> Customer </center> </h1>
<br/>
<form method="POST" action="/customer/upload" enctype="multipart/form-data">
    <input type="file" name="file" accept=".csv, .xlsx, .xls">
    <button type="submit">Upload</button>
</form>

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
    });
</script>

</body>
</html>