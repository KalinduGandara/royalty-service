<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>
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
    <h1>Add Product</h1>
    <form action="/saveProduct" method="post">
        <label for="name">Name:</label>
        <input type="text" id="name" name="name">

        <label for="code">Code:</label>
        <input type="text" id="code" name="code">

        <label for="capacity">Capacity:</label>
        <input type="text" id="capacity" name="capacity">

        <label for="description">Description:</label>
        <textarea id="description" name="description"></textarea>

        <label for="points">Points:</label>
        <input type="text" id="points" name="points">

        <input type="submit" value="Add Product">
    </form>
</body>
</html>
