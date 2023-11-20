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
            List<Customer> customerList = new ArrayList<>();

                   // Creating sample Customer objects
                   Customer customer1 = new Customer();
                   customer1.setId(1);
                   customer1.setName("Alice");
                   customer1.setNic("NIC111");
                   customer1.setPhone("111-111-1111");
                   customer1.setAddress("123 Street");
                   customer1.setArea("Area A");
                   customer1.setPoints(100);
                   customer1.setNotes("Regular customer");

                   Customer customer2 = new Customer();
                   customer2.setId(2);
                   customer2.setName("Bob");
                   customer2.setNic("NIC222");
                   customer2.setPhone("222-222-2222");
                   customer2.setAddress("456 Avenue");
                   customer2.setArea("Area B");
                   customer2.setPoints(150);
                   customer2.setNotes("VIP customer");

                   Customer customer3 = new Customer();
                   customer3.setId(3);
                   customer3.setName("Charlie");
                   customer3.setNic("NIC333");
                   customer3.setPhone("333-333-3333");
                   customer3.setAddress("789 Road");
                   customer3.setArea("Area C");
                   customer3.setPoints(200);
                   customer3.setNotes("Frequent customer");

                   Customer customer4 = new Customer();
                   customer4.setId(4);
                   customer4.setName("David");
                   customer4.setNic("NIC444");
                   customer4.setPhone("444-444-4444");
                   customer4.setAddress("1010 Lane");
                   customer4.setArea("Area D");
                   customer4.setPoints(75);
                   customer4.setNotes("New customer");

                   // Adding customers to the list
                   customerList.add(customer1);
                   customerList.add(customer2);
                   customerList.add(customer3);
                   customerList.add(customer4);

            for (Customer data : customerList) {
        %>
        <tr>
                <td><%= data.getName() %></td>
                <td><%= data.getNic() %></td>
                <td><%= data.getPhone() %></td>
                <td><%= data.getAddress() %></td>
                <td><%= data.getArea() %></td>
                <td><%= data.getPoints() %></td>
                <td><%= data.getNotes() %></td>
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
