<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="com.example.royalty.modal.Customer" %>
    <title>Customer</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.css">
        <style>
            .dataTables_filter {
                display: none; /* Hide the default search bar */
            }
        </style>
</head>
<body>
<h1> <center> Customer </center> </h1>
<br/>
<!-- Search inputs outside the table -->
<div>
    <label for="nameSearch">Search Name: </label>
    <input type="text" id="nameSearch" placeholder="Search Name">
    <label for="NICSearch">Search NIC: </label>
    <input type="text" id="NICSearch" placeholder="Search NIC">
    <label for="PhoneNoSearch">Search Phone No: </label>
    <input type="text" id="PhoneNoSearch" placeholder="Search Phone No">
</div>
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
                <td><%= customer.getName() %></td>
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
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>

<script>
    $(document).ready( function () {
        // Initialize DataTable
        var table = $('#dataTable').DataTable();

        // Function to apply search on specific columns
        function applyColumnSearch(inputSelector, columnIndex) {
            $(inputSelector).on('keyup change', function () {
                table.column(columnIndex).search(this.value).draw();
            });
        }

        // Apply search for Name and Code columns
        applyColumnSearch('#nameSearch', 0); // Replace '0' with the actual index of the Name column
        applyColumnSearch('#NICSearch', 1); // Replace '1' with the actual index of the Code column
        applyColumnSearch('#PhoneNoSearch', 2); // Replace '2' with the actual index of the Name column
    });
</script>

</body>
</html>
