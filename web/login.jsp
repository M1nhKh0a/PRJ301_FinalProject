<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login - Order Management System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .login-container {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 400px;
        }

        .login-container h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: bold;
        }

        .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            transition: border-color 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 5px rgba(102, 126, 234, 0.5);
        }

        .form-group input:invalid:required {
            border-color: #ff6b6b;
        }

        .error-message {
            color: #ff6b6b;
            font-size: 12px;
            margin-top: 4px;
            display: none;
        }

        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }

        .button-group button {
            flex: 1;
            padding: 12px;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .button-group button[type="submit"] {
            background-color: #667eea;
            color: white;
        }

        .button-group button[type="submit"]:hover {
            background-color: #5568d3;
        }

        .button-group button[type="reset"] {
            background-color: #e0e0e0;
            color: #333;
        }

        .button-group button[type="reset"]:hover {
            background-color: #d0d0d0;
        }

        .alert {
            margin-bottom: 20px;
            padding: 12px;
            border-radius: 4px;
            display: none;
        }

        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            display: block;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h1>Login</h1>
        
        <%
            String error = request.getParameter("error");
            if (error != null) {
                String message = "";
                if (error.equals("empty")) {
                    message = "Please fill out all fields.";
                } else if (error.equals("invalid")) {
                    message = "Invalid UserName & Password";
                } else if (error.equals("system")) {
                    message = "System error. Please try again.";
                }
        %>
        <div class="alert alert-danger"><%= message %></div>
        <% } %>
        
        <form action="LoginController" method="POST">
            <div class="form-group">
                <label for="username">User Name</label>
                <input type="text" id="username" name="username" required>
                <span class="error-message">estUser</span>
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
                <span class="error-message">estPass</span>
            </div>
            
            <div class="button-group">
                <button type="submit">Login</button>
                <button type="reset">Reset</button>
            </div>
        </form>
    </div>
</body>
</html>
