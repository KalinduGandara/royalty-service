<jsp:useBean id="endDate" scope="request" type="java.lang.String"/>
<jsp:useBean id="startDate" scope="request" type="java.lang.String"/>
<!DOCTYPE html>
<html>
<head>
    <%@ page import="java.util.List" %>
    <%@ page import="com.example.royalty.modal.Message" %>
    <%@ page import="java.time.format.DateTimeFormatter" %>
    <%@ page import="java.time.LocalDateTime" %>
    <title>View Messages</title>
    <!-- Include necessary CSS files -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/buttons.bootstrap4.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<%@include file="nav.jsp" %>
<h1> View Messages </h1>
<br/>
<form action="/message" method="get" class="form-inline">

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
