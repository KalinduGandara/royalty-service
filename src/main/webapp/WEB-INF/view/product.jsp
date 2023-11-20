<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="com.example.royalty.modal.Product" %>
    <title>Data Table Example</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.css">
</head>
<body>

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
                   product1.setName("Product 1");
                   product1.setCode("P001");
                   product1.setCapacity(100);
                   product1.setDescription("Description for Product 1");
                   product1.setPoints(50);

                   Product product2 = new Product();
                   product2.setId(2);
                   product2.setName("Product 2");
                   product2.setCode("P002");
                   product2.setCapacity(150);
                   product2.setDescription("Description for Product 2");
                   product2.setPoints(75);

                   // Adding products to the list
                   productList.add(product1);
                   product+List.add(product2);

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
    // Activate DataTable
    $(document).ready( function () {
        $('#dataTable').DataTable();
    });
</script>

</body>
</html>
