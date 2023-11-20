<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>
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
    <h1>Add Customer</h1>
    <form action="/customer/create" method="post">
        <label for="name">Name</label>
        <input type="text" name="name" id="name" required>
        <label for="nic">NIC</label>
        <input type="text" name="nic" id="nic" required>
        <label for="phone">Phone</label>
        <input type="text" name="phone" id="phone" required>
        <label for="address">Address</label>
        <input type="text" name="address" id="address" required>
        <label for="area">Area</label>
        <input type="text" name="area" id="area" required>
        <label for="points">Points</label>
        <input type="number" name="points" id="points" required>
        <label for="notes">Notes</label>
        <textarea name="notes" id="notes" rows="5"></textarea>
        <br>

        <input type="submit" value="Add Customer">
    </form>
</body>
</html>
