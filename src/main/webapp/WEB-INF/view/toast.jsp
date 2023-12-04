<% if (request.getAttribute("message") != null && !request.getAttribute("message").toString().isEmpty()) {
    String message = request.getAttribute("message").toString();%>
<div class="alert alert-success alert-dismissible fade show" role="alert">
    <strong><%=message%>
    </strong>
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">x</span>
    </button>
</div>
<%} %>
<% if (request.getAttribute("error") != null && !request.getAttribute("error").toString().isEmpty()) {
    String error = request.getAttribute("error").toString();%>
<div class="alert alert-danger alert-dismissible fade show" role="alert">
    <strong><%=error%>
    </strong>
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">x</span>
    </button>
</div>
<%} %>