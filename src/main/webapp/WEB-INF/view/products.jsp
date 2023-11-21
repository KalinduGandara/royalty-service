<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Product" %>
    <title>Product</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
        <style>
            .dataTables_filter {
                display: none; /* Hide the default search bar */
            }
        </style>
</head>
<body>
<h1> <center> Product </center> </h1>
<br/>
<!-- Search inputs outside the table -->
<div>
    <label for="nameSearch">Search Name: </label>
    <input type="text" id="nameSearch" placeholder="Search Name">
    <label for="codeSearch">Search Code: </label>
    <input type="text" id="codeSearch" placeholder="Search Code">
</div>
<br/>
<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>Name</th>
            <th>Code</th>
            <th>Capacity</th>
            <th>Description</th>
            <th>Points</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
           List<Product> products = (List<Product>) request.getAttribute("products");

            for (Product product : products) {
        %>
        <tr>
            <td><a href="/product/<%= product.getId()%>"><%= product.getName() %></a></td>
            <td><%= product.getCode() %></td>
            <td><%= product.getCapacity() %></td>
            <td><%= product.getDescription() %></td>
            <td><%= product.getPoints() %></td>
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

        // Function to apply search on specific columns
        function applyColumnSearch(inputSelector, columnIndex) {
            $(inputSelector).on('keyup change', function () {
                table.column(columnIndex).search(this.value).draw();
            });
        }

        // Apply search for Name and Code columns
        applyColumnSearch('#nameSearch', 0); // Replace '0' with the actual index of the Name column
        applyColumnSearch('#codeSearch', 1); // Replace '1' with the actual index of the Code column
    });
</script>

</body>
</html>
