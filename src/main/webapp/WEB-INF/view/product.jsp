<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="com.example.royalty.modal.Product" %>
    <title>Product</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.css">
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
           List<Product> productList = new ArrayList<>();

                   // Creating sample Product objects
                   Product product1 = new Product();
                   product1.setId(1);
                   product1.setName("ssss");
                   product1.setCode("P001");
                   product1.setCapacity(100);
                   product1.setDescription("Description for Product 1");
                   product1.setPoints(50);

                   Product product2 = new Product();
                   product2.setId(2);
                   product2.setName("dd");
                   product2.setCode("P002");
                   product2.setCapacity(150);
                   product2.setDescription("Description for Product 2");
                   product2.setPoints(75);

                   // Adding products to the list
                   productList.add(product1);
                   productList.add(product2);

            for (Product data : productList) {
        %>
        <tr>
            <td><%= data.getName() %></td>
            <td><%= data.getCode() %></td>
            <td><%= data.getCapacity() %></td>
            <td><%= data.getDescription() %></td>
            <td><%= data.getPoints() %></td>
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
        applyColumnSearch('#codeSearch', 1); // Replace '1' with the actual index of the Code column
    });
</script>

</body>
</html>
