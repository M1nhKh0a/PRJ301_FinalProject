package ssam.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.AnomalyDao;
import ssam.model.AnomalyDto;
import ssam.model.NotificationDao;
import ssam.model.NotificationDto;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 46. Ky su ghi nguyen nhan goc va bao da xu ly xong.

 * Nam dieu phai kiem tra:
 *   1. Vai tro phai la EN
 *   2. Ma ban ghi khong rong va ban ghi phai co that
 *   3. Ban ghi phai duoc giao cho CHINH nguoi dang dang nhap
 *   4. Trang thai phai la Assigned hoac Rejected
 *   5. Nguyen nhan goc khong duoc de trong va khong ngan hon 30 ky tu
 *
 * Dieu thu ba la dieu quan trong nhat ve mat an toan, giao dien co giau nut
 * o nhung dong khong phai cua minh nhung giau nut khong phai la chan.
 *
 * Xu ly xong thi gui thong bao cho toan bo QA dang hoat dong.
 *
 * Bon dieu bat buoc giu khi viet:
 *   1. Gan bien url bang duong lui truoc, chi doi khi that su thanh cong
 *   2. Chi chuyen tiep DUNG MOT LAN, o khoi finally
 *   3. Moi cau bao loi gom vao mot bien roi dat vao request duoi ten ERROR
 *   4. Danh tinh nguoi dung LUON lay tu phien lam viec, khong lay tu tham so
 */
@WebServlet(name = "ResolveAnomalyController", urlPatterns = {"/ResolveAnomalyController"})
public class ResolveAnomalyController extends HttpServlet {

    private static final String ERROR = "MainController?action=LoadAnomaly";
    private static final String SUCCESS = "MainController?action=LoadAnomaly";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = ERROR;
        try {
            //-----            your code here   --------------------------------
            HttpSession session = request.getSession(false);
            UserDto acc = (session == null) ? null : (UserDto) session.getAttribute("USER");

            if (acc == null) {
                request.setAttribute("ERROR", "Please login first");
            } else if (!acc.isEngineer()) {
                request.setAttribute("ERROR", "Only Engineers can resolve anomalies"); // Điều kiện 1[cite: 10]
            } else {
                String id = request.getParameter("id");
                String rootCause = request.getParameter("rootCause");

                AnomalyDao anomalyDao = new AnomalyDao();
                AnomalyDto anomaly = (id != null && !id.trim().isEmpty()) ? anomalyDao.getByID(id.trim()) : null;

                String message = null;

                if (id == null || id.trim().isEmpty() || anomaly == null) {
                    message = "Anomaly not found"; // Điều kiện 2[cite: 10]
                } else if (!acc.getUserID().equals(anomaly.getAssignedTo())) {
                    message = "You can only resolve anomalies assigned to you"; // Điều kiện 3[cite: 10]
                } else if (!"Assigned".equals(anomaly.getStatus()) && !"Rejected".equals(anomaly.getStatus())) {
                    message = "Status does not allow resolving this anomaly"; // Điều kiện 4[cite: 10]
                } else if (rootCause == null || rootCause.trim().length() < 30) {
                    message = "Root cause must be at least 30 characters"; // Điều kiện 5[cite: 10]
                }

                if (message != null) {
                    request.setAttribute("ERROR", message);
                } else {
                    if (anomalyDao.resolve(id.trim(), rootCause.trim())) {
                        NotificationDao notiDao = new NotificationDao();
                        UserDao userDao = new UserDao();
                        for (UserDto qa : userDao.getByRole("QA")) {
                            if (qa.isStatus()) { // Kiểm tra tài khoản đang hoạt động[cite: 10, 12]
                                notiDao.insert(new NotificationDto(notiDao.getNextID(), id.trim(), qa.getUserID(), "Anomaly " + id.trim() + " is resolved and waiting for review", null, false));
                            }
                        }
                        url = SUCCESS + "&id=" + id.trim();
                    } else {
                        request.setAttribute("ERROR", "Failed to resolve anomaly");
                    }
                }
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("Error at ResolveAnomalyController: " + e.toString());
            request.setAttribute("ERROR", "System error, please try again");
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
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
        return "ResolveAnomalyController";
    }
}
