<jsp:useBean id="endDate" scope="request" type="java.lang.String"/>
<jsp:useBean id="startDate" scope="request" type="java.lang.String"/>
<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.ReceivedMessage" %>
    <%@ page import="java.time.format.DateTimeFormatter" %>
    <title>View Messages</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/buttons.bootstrap4.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
          <meta charset="UTF-8">
</head>
<body>
<%@include file="nav.jsp" %>
<h1> View Received Messages </h1>
<br/>
<form action="/message/received" method="get" class="form-inline">
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
    <button type="submit" class="btn btn-primary mb-2">Filter</button>
</form>

<table id="dataTable" class="display">
    <thead>
    <tr>
        <th>Message</th>
        <th>Phone</th>
        <th>Received Time</th>
        <th>Status</th>
    </tr>
    </thead>
    <tbody>
    <!-- Here, you can dynamically populate the table rows with data -->
    <%
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ReceivedMessage> messages = (List<ReceivedMessage>) request.getAttribute("messages");

        for (ReceivedMessage message : messages) {
            String receivedTime = message.getReceivedTime() == null ? "" : message.getReceivedTime().format(formatter);
            String status = message.getStatus()==null ? "" : message.getStatus();
            Long cid = message.getCid();

    %>
    <tr>
        <td><%= message.getMessage() %></td>
        <td>
        <% if (cid != null) { %>
            <a href="/customer/<%= cid%>"> <%= message.getPhone() %> </a>
        <% }else {%>
            <%= message.getPhone() %>
        <% } %>
        </td>
        <td><%= receivedTime %></td>
        <td><%= status %></td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>

<!-- Include necessary JS files -->
<%@include file="footer.jsp" %>

<script>
    $(document).ready(function () {
        // Initialize DataTable
        const table = $('#dataTable').DataTable({
            dom: 'Bfrtip',
            buttons: [
                'csv', 'excel'
            ]
        });
        $('#search').on('keyup', function () {
            table.search(this.value).draw();
        });
    });
</script>

</body>
</html>
