<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controller.OrderController"%>
<%@page import="model.Order"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Welcome to Administrator</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }

        header {
            background-color: #2c3e50;
            color: white;
            padding: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        header h1 {
            font-size: 24px;
        }

        header .user-info {
            display: flex;
            gap: 15px;
            align-items: center;
        }

        header a {
            color: white;
            text-decoration: none;
            padding: 8px 15px;
            background-color: #e74c3c;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        header a:hover {
            background-color: #c0392b;
        }

        .container {
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }

        .filter-section {
            background: white;
            padding: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }

        .filter-section h2 {
            margin-bottom: 15px;
            color: #2c3e50;
        }

        .filter-buttons {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .filter-buttons input[type="radio"] {
            display: none;
        }

        .filter-buttons label {
            padding: 10px 20px;
            border: 2px solid #3498db;
            border-radius: 4px;
            cursor: pointer;
            background-color: white;
            color: #3498db;
            font-weight: bold;
            transition: all 0.3s;
        }

        .filter-buttons input[type="radio"]:checked + label {
            background-color: #3498db;
            color: white;
        }

        .table-section {
            background: white;
            padding: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .table-section h2 {
            margin-bottom: 15px;
            color: #2c3e50;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        table thead {
            background-color: #34495e;
            color: white;
        }

        table th,
        table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ecf0f1;
        }

        table tbody tr:hover {
            background-color: #f9f9f9;
        }

        .badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            color: white;
        }

        .badge-newly {
            background-color: #3498db;
        }

        .badge-pending {
            background-color: #f39c12;
        }

        .badge-delivered {
            background-color: #27ae60;
        }

        .badge-rejected {
            background-color: #e74c3c;
        }

        .action-btn {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            font-weight: bold;
            transition: background-color 0.3s;
        }

        .action-btn-delete {
            background-color: #e74c3c;
            color: white;
        }

        .action-btn-delete:hover {
            background-color: #c0392b;
        }

        .total-section {
            margin-top: 20px;
            padding: 15px;
            background-color: #ecf0f1;
            border-radius: 4px;
            text-align: right;
            font-weight: bold;
            color: #2c3e50;
        }

        .search-bar {
            margin-bottom: 15px;
        }

        .search-bar input {
            width: 100%;
            padding: 10px;
            border: 1px solid #bdc3c7;
            border-radius: 4px;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <%
        // Check session
        Integer userRole = (Integer) session.getAttribute("role");
        String userName = (String) session.getAttribute("userName");
        String fullName = (String) session.getAttribute("fullName");
        
        if (userRole == null) {
            response.sendRedirect("../login.jsp");
            return;
        }
    %>

    <header>
        <h1>Welcome to Administrator</h1>
        <div class="user-info">
            <span>Hi, <%= fullName %></span>
            <a href="../LogoutController">Logout</a>
        </div>
    </header>

    <div class="container">
        <div class="filter-section">
            <h2>Filter order by status:</h2>
            <form method="GET" style="margin-bottom: 15px;">
                <div class="filter-buttons">
                    <input type="radio" id="newly" name="status" value="0" onchange="this.form.submit()">
                    <label for="newly">● Newly orders</label>
                    
                    <input type="radio" id="rejected" name="status" value="3" onchange="this.form.submit()">
                    <label for="rejected">● Rejected orders</label>
                </div>
            </form>
        </div>

        <div class="table-section">
            <%
                int status = -1;
                String statusParam = request.getParameter("status");
                if (statusParam != null && !statusParam.isEmpty()) {
                    status = Integer.parseInt(statusParam);
                }
                
                List<Order> orders;
                String heading = "All Orders";
                
                if (status != -1) {
                    orders = OrderController.getOrdersByStatus(status);
                    if (status == 0) {
                        heading = "Newly created orders";
                    } else if (status == 3) {
                        heading = "Rejected orders";
                    }
                } else {
                    orders = OrderController.getAllOrders();
                }
                
                double totalAmount = 0;
            %>
            
            <h2><%= heading %></h2>
            
            <table>
                <thead>
                    <tr>
                        <th>No.</th>
                        <th>OrderID</th>
                        <th>OrderDate</th>
                        <th>Customer</th>
                        <th>Address</th>
                        <th>TotalAmount</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        int index = 1;
                        for (Order order : orders) {
                            String badgeClass = "badge-newly";
                            if (order.getStatus() == 1) badgeClass = "badge-pending";
                            else if (order.getStatus() == 2) badgeClass = "badge-delivered";
                            else if (order.getStatus() == 3) badgeClass = "badge-rejected";
                            
                            totalAmount += order.getTotalAmount();
                    %>
                    <tr>
                        <td><%= index++ %></td>
                        <td><%= order.getOrderID() %></td>
                        <td><%= order.getOrderDate() %></td>
                        <td><%= order.getCustomer() %></td>
                        <td><%= order.getAddress() %></td>
                        <td><%= String.format("%.0f", order.getTotalAmount()) %></td>
                        <td><span class="badge <%= badgeClass %>"><%= order.getStatusString() %></span></td>
                        <td>
                            <% if (order.getStatus() == 0 || order.getStatus() == 3) { %>
                                <button class="action-btn action-btn-delete" onclick="deleteOrder('<%= order.getOrderID() %>')">Delete</button>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            
            <div class="total-section">
                Total: <%= String.format("%.0f", totalAmount) %>
            </div>
        </div>
    </div>

    <script>
        function deleteOrder(orderID) {
            if (confirm('Are you sure you want to delete this order?')) {
                // Implementation for delete functionality
                alert('Delete order: ' + orderID);
            }
        }
    </script>
</body>
</html>
