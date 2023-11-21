<%@ page import="com.example.royalty.modal.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Product</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        h1 {
            text-align: center;
        }
        form {
            max-width: 400px;
            margin: 0 auto;
        }
        label {
            display: block;
            margin-bottom: 5px;
        }
        input[type="text"],
        textarea {
            width: calc(100% - 10px);
            padding: 5px;
            margin-bottom: 10px;
        }
        input[type="submit"] {
            padding: 8px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<% Product product = (Product) request.getAttribute("product");%>
    <h1>Update Product</h1>
    <form action="/product/create" method="post">
        <input type="hidden" name="id"  />
        <label for="name">Name:</label>
        <input required type="text" id="name" name="name" value="<%= product.getName()%>">

        <label for="code">Code:</label>
        <input required type="text" id="code" name="code" value="<%= product.getCode()%>">

        <label for="capacity">Capacity:</label>
        <input required type="number" id="capacity" name="capacity" value="<%= product.getCapacity()%>">

        <label for="description">Description:</label>
        <textarea id="description" name="description"><%= product.getDescription()%></textarea>

        <label for="points">Points:</label>
        <input required type="number" id="points" name="points" value="<%= product.getPoints()%>">

        <input type="submit" value="Add Product">
    </form>
</body>
</html>
