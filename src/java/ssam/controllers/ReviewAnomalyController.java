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
import ssam.model.InspectionDao;
import ssam.model.NotificationDao;
import ssam.model.NotificationDto;
import ssam.model.ReviewDao;
import ssam.model.ReviewDto;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 47. QA duyet ket qua xu ly, dat hoac tu choi.

 * Sau dieu phai kiem tra:
 *   1. Vai tro phai la QA
 *   2. Ma ban ghi khong rong va ban ghi phai co that
 *   3. Trang thai phai la Resolved
 *   4. Quyet dinh phai la Approve hoac Reject
 *   5. Tu choi thi bat buoc phai co y kien, dat thi khong bat buoc
 *   6. Ban ghi muc Critical chi duoc duyet dat khi da co it nhat MOT lan
 *      kiem tra cua QC voi ket qua Pass
 *
 * Dieu thu sau la rang buoc nghiep vu kho nhat cua bai, goi InspectionDao.countPass
 * de lay so lan Pass roi so sanh. Co so du lieu khong biet luat nay.
 *
 * Duyet dat thi doi trang thai sang Closed va gui thong bao cho nguoi bao ban dau
 * cung toan bo QC. Tu choi thi doi sang Rejected va gui thong bao cho ky su.
 *
 * Bon dieu bat buoc giu khi viet:
 *   1. Gan bien url bang duong lui truoc, chi doi khi that su thanh cong
 *   2. Chi chuyen tiep DUNG MOT LAN, o khoi finally
 *   3. Moi cau bao loi gom vao mot bien roi dat vao request duoi ten ERROR
 *   4. Danh tinh nguoi dung LUON lay tu phien lam viec, khong lay tu tham so
 */
@WebServlet(name = "ReviewAnomalyController", urlPatterns = {"/ReviewAnomalyController"})
public class ReviewAnomalyController extends HttpServlet {

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
            } else if (!acc.isQa()) { // Check vai tro QA[cite: 11, 12]
                request.setAttribute("ERROR", "Only QA can review anomalies"); // Dieukien 1[cite: 11]
            } else {
                String id = request.getParameter("id");
                String decision = request.getParameter("decision");
                String comment = request.getParameter("comment");

                AnomalyDao anomalyDao = new AnomalyDao();
                InspectionDao inspectionDao = new InspectionDao();

                AnomalyDto anomaly = (id != null && !id.trim().isEmpty()) ? anomalyDao.getByID(id.trim()) : null;

                String message = null;

                if (id == null || id.trim().isEmpty() || anomaly == null) {
                    message = "Anomaly not found"; // Dieukien 2[cite: 11]
                } else if (!"Resolved".equals(anomaly.getStatus())) {
                    message = "Only resolved anomalies can be reviewed"; // Dieukien 3[cite: 11]
                } else if (!"Approve".equals(decision) && !"Reject".equals(decision)) {
                    message = "Decision must be Approve or Reject"; // Dieukien 4[cite: 11]
                } else if ("Reject".equals(decision) && (comment == null || comment.trim().isEmpty())) {
                    message = "Comment is required when rejecting"; // Dieukien 5[cite: 11]
                } else if ("Approve".equals(decision) && "Critical".equals(anomaly.getSeverity()) && inspectionDao.countPass(id.trim()) == 0) {
                    message = "Critical anomaly requires at least one passed QC inspection"; // Dieukien 6[cite: 11]
                }

                if (message != null) {
                    request.setAttribute("ERROR", message);
                } else {
                    ReviewDao reviewDao = new ReviewDao();
                    String newRevID = reviewDao.getNextID();
                    ReviewDto rev = new ReviewDto(newRevID, id.trim(), acc.getUserID(), decision, comment, null);

                    if (reviewDao.insert(rev)) {
                        String newStatus = "Approve".equals(decision) ? "Closed" : "Rejected";
                        anomalyDao.changeStatus(id.trim(), newStatus);

                        NotificationDao notiDao = new NotificationDao();
                        UserDao userDao = new UserDao();

                        if ("Approve".equals(decision)) {
                            notiDao.insert(new NotificationDto(notiDao.getNextID(), id.trim(), anomaly.getReportedBy(), "Anomaly " + id.trim() + " was closed", null, false));
                            for (UserDto qc : userDao.getByRole("QC")) {
                                if (qc.isStatus()) { // Dung qc.isStatus() thay cho qc.isActive()[cite: 12]
                                    notiDao.insert(new NotificationDto(notiDao.getNextID(), id.trim(), qc.getUserID(), "Anomaly " + id.trim() + " was closed, please inspect", null, false));
                                }
                            }
                        } else {
                            if (anomaly.getAssignedTo() != null) {
                                notiDao.insert(new NotificationDto(notiDao.getNextID(), id.trim(), anomaly.getAssignedTo(), "Anomaly " + id.trim() + " was rejected by QA", null, false));
                            }
                        }

                        url = SUCCESS + "&id=" + id.trim();
                    } else {
                        request.setAttribute("ERROR", "Failed to review anomaly");
                    }
                }
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("Error at ReviewAnomalyController: " + e.toString());
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
        return "ReviewAnomalyController";
    }
}
