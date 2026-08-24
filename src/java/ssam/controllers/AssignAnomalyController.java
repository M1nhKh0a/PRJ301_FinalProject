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
 * TODO 45. Giao hoac giao lai mot ban ghi cho ky su.

 * Nam dieu phai kiem tra:
 *   1. Vai tro phai la AD hoac EN
 *   2. Ma ban ghi khong rong va ban ghi phai co that
 *   3. Trang thai phai la New, Assigned hoac Rejected, da Resolved hoac Closed
 *      thi khong giao lai duoc nua
 *   4. Ky su duoc chon phai co that, phai co vai tro EN va dang hoat dong
 *   5. Ky su duoc chon phai khac ky su dang giu viec hien tai
 *
 * Giao xong thi gui mot thong bao cho ky su moi.
 *
 * Bon dieu bat buoc giu khi viet:
 *   1. Gan bien url bang duong lui truoc, chi doi khi that su thanh cong
 *   2. Chi chuyen tiep DUNG MOT LAN, o khoi finally
 *   3. Moi cau bao loi gom vao mot bien roi dat vao request duoi ten ERROR
 *   4. Danh tinh nguoi dung LUON lay tu phien lam viec, khong lay tu tham so
 */
@WebServlet(name = "AssignAnomalyController", urlPatterns = {"/AssignAnomalyController"})
public class AssignAnomalyController extends HttpServlet {

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
            } else if (!acc.isAdmin() && !acc.isEngineer()) {
                request.setAttribute("ERROR", "Only Admin and Engineer can assign tasks"); // Dieukien 1[cite: 6]
            } else {
                String id = request.getParameter("id");
                String engineerID = request.getParameter("engineerID");

                AnomalyDao anomalyDao = new AnomalyDao();
                UserDao userDao = new UserDao();

                AnomalyDto anomaly = (id != null && !id.trim().isEmpty()) ? anomalyDao.getByID(id.trim()) : null;
                UserDto engineer = (engineerID != null && !engineerID.trim().isEmpty()) ? userDao.getByID(engineerID.trim()) : null;

                String message = null;

                if (id == null || id.trim().isEmpty() || anomaly == null) {
                    message = "Anomaly not found"; // Dieukien 2[cite: 6]
                } else if ("Resolved".equals(anomaly.getStatus()) || "Closed".equals(anomaly.getStatus())) {
                    message = "Cannot reassign a resolved or closed anomaly"; // Dieukien 3[cite: 6]
                } else if (engineer == null || !engineer.isEngineer() || !engineer.isStatus()) {
                    message = "Invalid or inactive engineer selected"; // Dieukien 4 (dung engineer.isStatus())[cite: 6, 12]
                } else if (engineerID.trim().equals(anomaly.getAssignedTo())) {
                    message = "Engineer is already assigned to this anomaly"; // Dieukien 5[cite: 6]
                }

                if (message != null) {
                    request.setAttribute("ERROR", message);
                } else {
                    if (anomalyDao.assign(id.trim(), engineerID.trim())) {
                        NotificationDao notiDao = new NotificationDao();
                        notiDao.insert(new NotificationDto(notiDao.getNextID(), id.trim(), engineerID.trim(), "You have been assigned to anomaly " + id.trim(), null, false)); //[cite: 6, 13]
                        
                        url = SUCCESS + "&id=" + id.trim();
                    } else {
                        request.setAttribute("ERROR", "Failed to assign anomaly");
                    }
                }
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("Error at AssignAnomalyController: " + e.toString());
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
        return "AssignAnomalyController";
    }
}
