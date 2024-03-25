<%@ page import="com.example.royalty.modal.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Product</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container-fluid">
    <%@include file="toast.jsp" %>
    <div class="row">
        <div class="col-md-6 p-3">
            <h2>View Product</h2>

            <%--@elvariable id="product" type="com.example.royalty.modal.Product"--%>
            <form:form action="/product/${product.id}" method="post" modelAttribute="product">
                <div class="form-group">
                    <label for="name">Name:</label>
                    <form:input path="name" type="text" id="name" name="name" class="form-control"/>
                    <form:errors path="name" cssClass="text-danger"/>
                </div>
                <div class="form-group">
                    <label for="code">Code:</label>
                    <form:hidden path="code"/>
                    <input disabled value="${product.code}" type="text" id="code" name="code" class="form-control"/>
                    <form:errors path="code" cssClass="text-danger"/>
                </div>
                <div class="form-group">
                    <label for="capacity">Capacity:</label>
                    <form:input path="capacity" type="number" id="capacity" name="capacity" class="form-control"/>
                    <form:errors path="capacity" cssClass="text-danger"/>
                </div>
                <div class="form-group">
                    <label for="description">Description:</label>
                    <form:textarea path="description" id="description" name="description"
                                   class="form-control"/>
                </div>
                <div class="form-group">
                    <label for="points">Points:</label>
                    <form:input path="points" type="number" id="points" name="points" class="form-control"/>
                    <form:errors path="points" cssClass="text-danger"/>
                </div>
                <button type="submit" class="btn btn-primary">Update Product</button>
            </form:form>
        </div>

        <div class="col-md-6 p-3">
            <%-- <h2>Generate Codes</h2>
             &lt;%&ndash;@elvariable id="code" type="com.example.royalty.dao.GenerateCodeDAO"&ndash;%&gt;
             <form:form action="/product/generate" method="post" modelAttribute="code">
                 <div class="form-group">
                     <label for="count">Count</label>
                     <form:hidden path="productId" value="${product.id}"/>
                     <form:input path="count" type="text" id="count" name="count" class="form-control" required="true"/>
                     <form:errors path="count" cssClass="text-danger"/>
                 </div>

                 <button type="submit" class="btn btn-primary">Generate Codes</button>
             </form:form>
             <hr/>
             <a href="/product/${product.id}/codes" class="btn btn-primary">View Codes</a>
             &lt;%&ndash;            <a href="/product/${product.id}/generate" class="btn btn-primary">Generate Codes</a>&ndash;%&gt;
             --%>
            <h2>Generate Codes</h2>
            <form id="csvForm" class="form-inline">
                <div class="input-group mb-2 mr-sm-2">
                    <div class="input-group-prepend">
                        <div class="input-group-text"><label for="count">Count:</label></div>
                    </div>
                    <input class="form-control" type="number" id="count" name="rowCount" min="1" required>
                </div>
                <button class="btn btn-primary mb-2" type="submit">Download CSV</button>
            </form>
                <form action="/product/${product.id}/codes" method="get" class="form-inline">
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
                    <button type="submit" class="btn btn-primary mb-2">View Codes</button>
                </form>
        </div>
    </div>
</div>
<%--<script src="${pageContext.request.contextPath}/js/jquery-3.7.1.min.js"></script>--%>
<%@include file="footer.jsp" %>
<script>
    $(document).ready(function () {
        $('#csvForm').submit(function (e) {
            e.preventDefault();


            const csrfToken = $("meta[name='_csrf']").attr("content");
            const csrfHeader = $("meta[name='_csrf_header']").attr("content");


            const count = $('#count').val();

            $.ajax({
                url: '/product/generate',
                type: 'POST',
                data: {count: count, productId: ${product.id}},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (data, status, xhr) {
                    // Get the filename from the Content-Disposition header
                    let filename = "";
                    const contentDisposition = xhr.getResponseHeader('Content-Disposition');
                    if (contentDisposition && contentDisposition.indexOf('attachment') !== -1) {
                        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                        const matches = filenameRegex.exec(contentDisposition);
                        if (matches != null && matches[1]) {
                            filename = matches[1].replace(/['"]/g, '');
                        }
                    }

                    const blob = new Blob([data], {type: 'text/csv'});
                    const link = document.createElement('a');
                    link.href = window.URL.createObjectURL(blob);
                    if (filename){
                        link.download = filename;
                    }
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);


                    $('#count').val('');
                }
            });
        });
    });

</script>

</body>
</html>
