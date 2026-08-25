package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Order;
import utils.DBConnection;

public class OrderController extends HttpServlet {

    public static List<Order> getOrdersByStatus(int status) {
        List<Order> orders = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM tblOrders WHERE status = ? ORDER BY orderDate DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderID(rs.getString("orderID"));
                order.setOrderDate(rs.getDate("orderDate"));
                order.setCustomer(rs.getString("customer"));
                order.setAddress(rs.getString("address"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                order.setStatus(rs.getInt("status"));
                orders.add(order);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM tblOrders ORDER BY orderDate DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderID(rs.getString("orderID"));
                order.setOrderDate(rs.getDate("orderDate"));
                order.setCustomer(rs.getString("customer"));
                order.setAddress(rs.getString("address"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                order.setStatus(rs.getInt("status"));
                orders.add(order);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static void updateOrderStatus(String orderID, int newStatus) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE tblOrders SET status = ? WHERE orderID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newStatus);
            pstmt.setString(2, orderID);
            
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServletInfo() {
        return "Order Controller";
    }
}
