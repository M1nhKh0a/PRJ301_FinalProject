package ssam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.EquipmentDao;
import ssam.model.EquipmentDto;
import ssam.model.UserDto;
import ssam.utils.DbUtils;

/**
 * TODO 52. DA LAM. Doi trang thai thiet bi.
 *
 * Dieu kien thu nam la rang buoc nghiep vu ma co so du lieu khong biet:
 * khong duoc chuyen thiet bi sang Retired khi thiet bi do con ban ghi bat
 * thuong chua dong, vi nhu vay se con viec treo lai ma khong ai xu ly.
 */
@WebServlet(name = "ChangeEquipmentStatusController",
            urlPatterns = {"/ChangeEquipmentStatusController"})
public class ChangeEquipmentStatusController extends HttpServlet {

    private static final String SEARCH = "MainController?action=SearchEquipment";

    private static final String COUNT_OPEN
            = "SELECT COUNT(*) AS total FROM Anomalies "
            + "WHERE equipmentID = ? AND status <> N'Closed'";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = SEARCH;
        try {
            HttpSession session = request.getSession(false);
            UserDto acc = (session == null) ? null : (UserDto) session.getAttribute("USER");

            String message = null;
            if (acc == null || !acc.isAdmin()) {
                message = "You are not allowed to do this";
                url = "login.jsp";
            } else {
                String id = request.getParameter("equipmentID");
                String status = request.getParameter("newStatus");
                EquipmentDao dao = new EquipmentDao();
                EquipmentDto e = null;

                if (id == null || id.trim().isEmpty()) {
                    message = "Missing equipment id";
                } else if ((e = dao.getByID(id.trim())) == null) {
                    message = "Equipment not found";
                } else if (!"Running".equals(status) && !"Maintenance".equals(status)
                        && !"Retired".equals(status)) {
                    message = "Invalid status";
                } else if (status.equals(e.getStatus())) {
                    message = "Equipment is already in this status";
                } else if ("Retired".equals(status) && countOpenAnomaly(id.trim()) > 0) {
                    message = "This equipment still has open anomalies";
                }

                if (message == null && dao.changeStatus(id.trim(), status)) {
                    request.setAttribute("MSG", "Status changed to " + status);
                } else if (message == null) {
                    message = "Change status failed";
                }
            }
            if (message != null) {
                request.setAttribute("ERROR", message);
            }
        } catch (Exception e) {
            log("Error at ChangeEquipmentStatusController: " + e.toString());
            request.setAttribute("ERROR", "System error, please try again");
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /** Dem ban ghi bat thuong chua dong cua mot thiet bi. */
    private int countOpenAnomaly(String equipmentID) throws Exception {
        int total = 0;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(COUNT_OPEN);
                st.setString(1, equipmentID);
                rs = st.executeQuery();
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return total;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Change equipment status";
    }
}
