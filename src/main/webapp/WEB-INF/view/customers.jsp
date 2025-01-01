<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Customer" %>
    <title>Customer</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/buttons.bootstrap4.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
          <meta charset="UTF-8">
</head>
<body>
<%@include file="nav.jsp" %>
<h1>  Customer  </h1>
<br/>
<%@include file="toast.jsp" %>
<%--@elvariable id="upload" type="com.example.royalty.dao.BulkUploadDAO"--%>
<from:form method="post" action="/customer/upload" enctype="multipart/form-data" modelAttribute="upload">
    <form:input  id="file" type="file" accept=".csv, .xlsx, .xls" path="file" required="true"/>
    <form:errors path="file" cssClass="text-danger"/>
    <button class="btn btn-primary" type="submit">Upload</button>
</from:form>

<br/>
<table id="dataTable" class="display">
    <thead>
        <tr>
            <th>TP Number</th>
            <th>Name</th>
            <th>Address</th>
            <th>Phone Number</th>
            <th>Available Points</th>
            <th>City</th>
            <th>District</th>
            <th>Province</th>
            <th>Sales person Territory </th>
            <th>Region </th>
            <th>Assigned CMDE </th>
            <th>Loyalty status </th>
            <th>Current Ave  consumption P/M  </th>
            <th>Linked dealer 1 </th>
            <th>Linked dealer 2 </th>
            <th>Notes</th>
        </tr>
    </thead>
    <tbody>
        <!-- Here, you can dynamically populate the table rows with data -->
        <%
            List<Customer> customers = (List<Customer>) request.getAttribute("customers");

            for (Customer customer : customers) {
                String address = customer.getAddress() == null ? "" : customer.getAddress();
                String city = customer.getCity() == null ? "" : customer.getCity();
                String district = customer.getDistrict() == null ? "" : customer.getDistrict();
                String province = customer.getProvince() == null ? "" : customer.getProvince();
                String salesPersonTerritory = customer.getSalesPersonTerritory() == null ? "" : customer.getSalesPersonTerritory();
                String region = customer.getRegion() == null ? "" : customer.getRegion();
                String assignedCMDE = customer.getAssignedCMDE() == null ? "" : customer.getAssignedCMDE();
                String loyaltyStatus = customer.getLoyaltyStatus() == null ? "" : customer.getLoyaltyStatus();
                String currentAveConsumptionPM = customer.getCurrentAveConsumptionPM() == null ? "" : customer.getCurrentAveConsumptionPM();
                String linkedDealer1 = customer.getLinkedDealer1() == null ? "" : customer.getLinkedDealer1();
                String linkedDealer2 = customer.getLinkedDealer2() == null ? "" : customer.getLinkedDealer2();
                String notes = customer.getNotes() == null ? "" : customer.getNotes();
        %>
        <tr>
                 <td><a href="/customer/<%= customer.getId()%>"><%= customer.getTpNumber() %></a></td>
                <td><%= customer.getName() %></td>
                <td><%= address %></td>
                <td><%= customer.getPhone() %></td>
                <td><%= customer.getPoints() %></td>
                <td><%= city %></td>
                <td><%= district %></td>
                <td><%= province %></td>
                <td><%= salesPersonTerritory %></td>
                <td><%= region %></td>
                <td><%= assignedCMDE %></td>
                <td><%= loyaltyStatus %></td>
                <td><%= currentAveConsumptionPM %></td>
                <td><%= linkedDealer1 %></td>
                <td><%= linkedDealer2 %></td>
                <td><%= notes %></td>
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
