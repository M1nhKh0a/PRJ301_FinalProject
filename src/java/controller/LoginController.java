package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.DBConnection;

public class LoginController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validate input is not empty
        if (userName == null || userName.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            response.sendRedirect("login.jsp?error=empty");
            return;
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM tblUsers WHERE userName = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("userName", rs.getString("userName"));
                session.setAttribute("fullName", rs.getString("fullName"));
                session.setAttribute("role", rs.getInt("role"));
                
                // Redirect to admin page
                response.sendRedirect("admin/adminMain.jsp");
            } else {
                // Login failed
                response.sendRedirect("login.jsp?error=invalid");
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=system");
        }
    }

    public String getServletInfo() {
        return "Login Controller";
    }
}
