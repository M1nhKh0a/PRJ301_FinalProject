<%--
    Document : login
    DA VIET SAN, khong phai sua.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Smart Semiconductor Anomaly Management</title>
    </head>
    <body>
        <h1>SMART SEMICONDUCTOR ANOMALY MANAGEMENT</h1>
        <h2>Log in</h2>
        <form action="MainController" method="POST">
            <p>User ID: <input type="text" name="userID" required=""/></p>
            <p>Password: <input type="password" name="password" required=""/></p>
            <p><input type="submit" name="action" value="Login"/></p>
        </form>
        <p style="color: red">${requestScope.ERROR}</p>
    </body>
</html>
