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
import ssam.model.InspectionDto;
import ssam.model.NotificationDao;
import ssam.model.NotificationDto;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 48. QC ghi nhan ket qua kiem tra mot lo hang.

 * Sau dieu phai kiem tra:
 *   1. Vai tro phai la QC
 *   2. Ma ban ghi khong rong va ban ghi phai co that
 *   3. Trang thai phai la Resolved hoac Closed, chua xu ly xong thi chua kiem tra
 *   4. Ma lo hang khong duoc de trong
 *   5. Lo hang do chua duoc ghi nhan cho ban ghi nay
 *   6. Ket qua phai la Pass, Fail hoac Rework
 *
 * Ket qua Fail thi gui them mot thong bao cho toan bo QA, vi ban ghi da dong
 * co the phai mo lai.
 *
 * Bon dieu bat buoc giu khi viet:
 *   1. Gan bien url bang duong lui truoc, chi doi khi that su thanh cong
 *   2. Chi chuyen tiep DUNG MOT LAN, o khoi finally
 *   3. Moi cau bao loi gom vao mot bien roi dat vao request duoi ten ERROR
 *   4. Danh tinh nguoi dung LUON lay tu phien lam viec, khong lay tu tham so
 */
@WebServlet(name = "InspectAnomalyController", urlPatterns = {"/InspectAnomalyController"})
public class InspectAnomalyController extends HttpServlet {

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
            } else if (!acc.isQc()) { // Cap nhat isQc()[cite: 8, 12]
                request.setAttribute("ERROR", "Only QC can record inspection results");
            } else {
                String id = request.getParameter("id");
                String lotID = request.getParameter("lotID");
                String result = request.getParameter("result");
                String note = request.getParameter("note");

                AnomalyDao anomalyDao = new AnomalyDao();
                InspectionDao inspectionDao = new InspectionDao();

                AnomalyDto anomaly = (id != null && !id.trim().isEmpty()) ? anomalyDao.getByID(id.trim()) : null;

                String message = null;

                if (id == null || id.trim().isEmpty() || anomaly == null) {
                    message = "Anomaly not found";
                } else if (!"Resolved".equals(anomaly.getStatus()) && !"Closed".equals(anomaly.getStatus())) {
                    message = "Cannot inspect an anomaly that is not resolved or closed";
                } else if (lotID == null || lotID.trim().isEmpty()) {
                    message = "Lot ID cannot be empty";
                } else if (inspectionDao.isLotInspected(id.trim(), lotID.trim())) {
                    message = "Lot ID has already been inspected for this anomaly";
                } else if (!"Pass".equals(result) && !"Fail".equals(result) && !"Rework".equals(result)) {
                    message = "Invalid inspection result";
                }

                if (message != null) {
                    request.setAttribute("ERROR", message);
                } else {
                    String newInspID = inspectionDao.getNextID();
                    InspectionDto insp = new InspectionDto(newInspID, id.trim(), acc.getUserID(), lotID.trim(), result, note, null);

                    if (inspectionDao.insert(insp)) {
                        if ("Fail".equals(result)) {
                            NotificationDao notiDao = new NotificationDao();
                            UserDao userDao = new UserDao();
                            for (UserDto qa : userDao.getByRole("QA")) {
                                if (qa.isStatus()) { // Dung kieu boolean status[cite: 12]
                                    notiDao.insert(new NotificationDto(notiDao.getNextID(), id.trim(), qa.getUserID(), "Inspection failed for lot " + lotID.trim() + " on anomaly " + id.trim(), null, false));
                                }
                            }
                        }
                        url = SUCCESS + "&id=" + id.trim();
                    } else {
                        request.setAttribute("ERROR", "Failed to record inspection");
                    }
                }
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("Error at InspectAnomalyController: " + e.toString());
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
        return "InspectAnomalyController";
    }
}
