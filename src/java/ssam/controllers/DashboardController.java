package ssam.controllers;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.AnomalyDao;
import ssam.model.AnomalyDto;
import ssam.model.NotificationDao;
import ssam.model.UserDto;

/**
 * TODO 41. Man hinh tong quan, noi dung doi theo vai tro.
 *
 * Du lieu can nap cho MOI vai tro: so thong bao chua doc cua chinh nguoi dang
 * nhap thong ke so ban ghi theo tung muc do nghiem trong so ban ghi theo tung
 * trang thai New, Assigned, Resolved
 *
 * Rieng ky su nap them danh sach ban ghi dang duoc giao cho minh, rieng QA nap
 * them danh sach ban ghi dang cho duyet.
 *
 * Phan thong ke PHAI lay bang ham gop trong cau lenh, khong duoc lay het ban
 * ghi ve roi dem trong Java.
 *
 * Bon dieu bat buoc giu khi viet: 1. Gan bien url bang duong lui truoc, chi doi
 * khi that su thanh cong 2. Chi chuyen tiep DUNG MOT LAN, o khoi finally 3. Moi
 * cau bao loi gom vao mot bien roi dat vao request duoi ten ERROR 4. Danh tinh
 * nguoi dung LUON lay tu phien lam viec, khong lay tu tham so
 */
@WebServlet(name = "DashboardController", urlPatterns = {"/DashboardController"})
public class DashboardController extends HttpServlet {

    private static final String ERROR = "login.jsp";
    private static final String SUCCESS = "dashboard.jsp";

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
            } else {
                AnomalyDao anomalyDao = new AnomalyDao();
                NotificationDao notiDao = new NotificationDao();

                // Du lieu dung chung cho moi vai tro[cite: 7]
                int unreadNoti = notiDao.countUnread(acc.getUserID());
                List<AnomalyDto> severityList = anomalyDao.countBySeverity();
                int countNew = anomalyDao.countByStatus("New");
                int countAssigned = anomalyDao.countByStatus("Assigned");
                int countResolved = anomalyDao.countByStatus("Resolved");

                request.setAttribute("UNREAD_NOTI", unreadNoti);
                request.setAttribute("SEVERITY_LIST", severityList);
                request.setAttribute("COUNT_NEW", countNew);
                request.setAttribute("COUNT_ASSIGNED", countAssigned);
                request.setAttribute("COUNT_RESOLVED", countResolved);

                // Nap du lieu rieng theo vai tro[cite: 7]
                if (acc.isEngineer()) {
                    List<AnomalyDto> assignedList = anomalyDao.search("", "", "", "", acc.getUserID());
                    request.setAttribute("ASSIGNED_LIST", assignedList);
                } else if (acc.isQa()) {
                    List<AnomalyDto> pendingReviewList = anomalyDao.search("", "", "", "Resolved", "");
                    request.setAttribute("PENDING_REVIEW_LIST", pendingReviewList);
                }

                url = SUCCESS;
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("Error at DashboardController: " + e.toString());
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
        return "DashboardController";
    }
}
