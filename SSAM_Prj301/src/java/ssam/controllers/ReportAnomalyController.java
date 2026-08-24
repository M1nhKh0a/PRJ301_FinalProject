package ssam.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.AnomalyDao;
import ssam.model.AnomalyDto;
import ssam.model.EquipmentDao;
import ssam.model.EquipmentDto;
import ssam.model.NotificationDao;
import ssam.model.NotificationDto;
import ssam.model.ShiftDao;
import ssam.model.ShiftDto;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 43. Bao mot bat thuong moi, kem viec giao ca truc va gui thong bao.

 * Day la chuc nang phuc tap nhat cua bai, gom ba phan noi tiep nhau.
 *
 * PHAN MOT, kiem tra du lieu, sau dieu:
 *   1. Vai tro phai la OP hoac EN, QA va QC khong duoc bao
 *   2. Ma thiet bi khong rong va thiet bi phai co that
 *   3. Thiet bi KHONG duoc o trang thai Retired
 *   4. Muc do nghiem trong phai la mot trong bon gia tri hop le
 *   5. Mo ta khong duoc de trong va khong duoc ngan hon 20 ky tu
 *   6. Nguoi bao phai thuoc cung khu vuc voi thiet bi, tru khi la EN
 *
 * PHAN HAI, giao viec tu dong:
 *   Xac dinh ca hien tai theo gio he thong, tu 6 gio den truoc 18 gio la ca
 *   Day, con lai la ca Night. Goi ShiftDao.findOnDuty voi khu vuc cua thiet bi,
 *   ngay hom nay va ca vua xac dinh.
 *   Tim duoc ky su thi ghi ban ghi roi goi AnomalyDao.assign de chuyen sang
 *   trang thai Assigned. Khong tim duoc thi giu nguyen trang thai New.
 *
 * PHAN BA, gui thong bao:
 *   Co ky su truc      gui mot thong bao cho chinh ky su do
 *   Khong co ky su truc gui thong bao cho TOAN BO ky su dang hoat dong
 *   Muc Critical        gui them thong bao cho toan bo QA
 *
 * Ma ban ghi sinh tu dong, ma nguoi bao lay tu phien lam viec.
 *
 * Bon dieu bat buoc giu khi viet:
 *   1. Gan bien url bang duong lui truoc, chi doi khi that su thanh cong
 *   2. Chi chuyen tiep DUNG MOT LAN, o khoi finally
 *   3. Moi cau bao loi gom vao mot bien roi dat vao request duoi ten ERROR
 *   4. Danh tinh nguoi dung LUON lay tu phien lam viec, khong lay tu tham so
 */
@WebServlet(name = "ReportAnomalyController", urlPatterns = {"/ReportAnomalyController"})
public class ReportAnomalyController extends HttpServlet {

    private static final String ERROR = "reportAnomaly.jsp";
    private static final String SUCCESS = "MainController?action=SearchAnomaly";

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
            } else if (!acc.isOperator() && !acc.isEngineer()) {
                request.setAttribute("ERROR", "Only operators and engineers can report anomalies"); // Dieukien 1[cite: 9]
            } else {
                String equipmentID = request.getParameter("equipmentID");
                String severity = request.getParameter("severity");
                String description = request.getParameter("description");

                EquipmentDao equipmentDao = new EquipmentDao();
                EquipmentDto equipment = (equipmentID != null && !equipmentID.trim().isEmpty()) ? equipmentDao.getByID(equipmentID.trim()) : null;

                String message = null;

                // PHAN MOT: Kiem tra du lieu[cite: 9]
                if (equipmentID == null || equipmentID.trim().isEmpty() || equipment == null) {
                    message = "Equipment not found"; // Dieukien 2[cite: 9]
                } else if ("Retired".equalsIgnoreCase(equipment.getStatus())) {
                    message = "This equipment is retired"; // Dieukien 3[cite: 9]
                } else if (severity == null || (!severity.equals("Low") && !severity.equals("Medium") && !severity.equals("High") && !severity.equals("Critical"))) {
                    message = "Invalid severity level"; // Dieukien 4[cite: 9]
                } else if (description == null || description.trim().length() < 20) {
                    message = "Description must be at least 20 characters"; // Dieukien 5[cite: 9]
                } else if (!acc.isEngineer() && !equipment.getArea().equalsIgnoreCase(acc.getArea())) {
                    message = "You can only report equipment in your area"; // Dieukien 6[cite: 9]
                }

                if (message != null) {
                    request.setAttribute("ERROR", message);
                } else {
                    AnomalyDao anomalyDao = new AnomalyDao();
                    String newID = anomalyDao.getNextID();

                    // Tao ban ghi[cite: 9]
                    AnomalyDto dto = new AnomalyDto(newID, equipmentID.trim(), acc.getUserID(), null, severity, description.trim(), "New", null, null, null);
                    if (anomalyDao.insert(dto)) {
                        
                        // PHAN HAI: Giao viec tu dong theo ca[cite: 9]
                        Calendar now = Calendar.getInstance();
                        int hour = now.get(Calendar.HOUR_OF_DAY);
                        String shiftType = (hour >= 6 && hour < 18) ? "Day" : "Night";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String today = sdf.format(now.getTime());

                        ShiftDao shiftDao = new ShiftDao();
                        ShiftDto onDuty = shiftDao.findOnDuty(equipment.getArea(), today, shiftType);

                        boolean assigned = false;
                        if (onDuty != null) {
                            assigned = anomalyDao.assign(newID, onDuty.getUserID());
                        }

                        // PHAN BA: Gui thong bao[cite: 9]
                        NotificationDao notiDao = new NotificationDao();
                        UserDao userDao = new UserDao();

                        if (assigned && onDuty != null) {
                            notiDao.insert(new NotificationDto(notiDao.getNextID(), newID, onDuty.getUserID(), "You have been assigned to anomaly " + newID, null, false)); //[cite: 9, 13]
                        } else {
                            for (UserDto en : userDao.getByRole("EN")) {
                                if (en.isStatus()) { // Dung en.isStatus()[cite: 12]
                                    notiDao.insert(new NotificationDto(notiDao.getNextID(), newID, en.getUserID(), "New anomaly " + newID + " needs an engineer", null, false)); //[cite: 9, 13]
                                }
                            }
                        }

                        if ("Critical".equals(severity)) {
                            for (UserDto qa : userDao.getByRole("QA")) {
                                if (qa.isStatus()) { // Dung qa.isStatus()[cite: 12]
                                    notiDao.insert(new NotificationDto(notiDao.getNextID(), newID, qa.getUserID(), "Critical anomaly " + newID + " reported", null, false)); //[cite: 9, 13]
                                }
                            }
                        }

                        url = SUCCESS;
                    } else {
                        request.setAttribute("ERROR", "Failed to report anomaly");
                    }
                }
            }
            //-----            your code here   --------------------------------
        } catch (Exception e) {
            log("Error at ReportAnomalyController: " + e.toString());
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
        return "ReportAnomalyController";
    }
}
