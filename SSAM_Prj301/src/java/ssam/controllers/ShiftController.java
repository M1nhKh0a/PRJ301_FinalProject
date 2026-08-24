package ssam.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.NotificationDao;
import ssam.model.ShiftDao;
import ssam.model.ShiftDto;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 53. DA LAM. Lo hai chang cua phan lich truc ca.
 *
 * Bang Shifts nuoi du lieu cho co che giao viec tu dong, nen phan nay phai
 * xong truoc khi lam chuc nang bao bat thuong.
 */
@WebServlet(name = "ShiftController", urlPatterns = {"/ShiftController"})
public class ShiftController extends HttpServlet {

    private static final String LIST_PAGE = "shiftList.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = LIST_PAGE;
        try {
            HttpSession session = request.getSession(false);
            UserDto acc = (session == null) ? null : (UserDto) session.getAttribute("USER");

            if (acc == null || !acc.isAdmin()) {
                request.setAttribute("ERROR", "You are not allowed to do this");
                url = "login.jsp";
            } else {
                ShiftDao dao = new ShiftDao();
                if ("SaveShift".equals(request.getParameter("action"))) {
                    handleSave(request, dao);
                }
                loadList(request, dao);
                request.setAttribute("LIST_ENGINEER", new UserDao().getByRole("EN"));
                request.setAttribute("UNREAD",
                        new NotificationDao().countUnread(acc.getUserID()));
            }
        } catch (Exception e) {
            log("Error at ShiftController: " + e.toString());
            request.setAttribute("ERROR", "System error, please try again");
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private void loadList(HttpServletRequest request, ShiftDao dao) throws Exception {
        String from = request.getParameter("fromDate");
        String to = request.getParameter("toDate");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        if (from == null || from.trim().isEmpty()) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -7);
            from = f.format(c.getTime());
        }
        if (to == null || to.trim().isEmpty()) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 30);
            to = f.format(c.getTime());
        }
        request.setAttribute("LIST_SHIFT", dao.searchByDate(from, to));
        request.setAttribute("fromDate", from);
        request.setAttribute("toDate", to);
    }

    /** Chang ghi: kiem tra sau dieu roi them mot ca truc. */
    private void handleSave(HttpServletRequest request, ShiftDao dao) throws Exception {
        String userID = request.getParameter("userID");
        String shiftDate = request.getParameter("shiftDate");
        String shiftType = request.getParameter("shiftType");
        String area = request.getParameter("area");

        String message = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;

        // Dieu 2: tai khoan phai co that, dung vai tro va dang hoat dong
        if (userID == null || userID.trim().isEmpty()) {
            message = "Please choose an engineer";
        } else {
            UserDto u = new UserDao().getByID(userID.trim());
            if (u == null) {
                message = "Engineer not found";
            } else if (!u.isEngineer()) {
                message = "Only engineers can be scheduled";
            } else if (!u.isStatus()) {
                message = "This account is disabled";
            }
        }
        // Dieu 3 va 4: ngay dung dinh dang va khong o qua khu
        if (message == null) {
            try {
                f.setLenient(false);
                d = f.parse(shiftDate.trim());
            } catch (Exception ex) {
                message = "Shift date is not valid";
            }
        }
        if (message == null) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            if (d.before(today.getTime())) {
                message = "Shift date must not be in the past";
            }
        }
        // Dieu 5
        if (message == null && !"Day".equals(shiftType) && !"Night".equals(shiftType)) {
            message = "Shift type must be Day or Night";
        }
        if (message == null && (area == null || area.trim().isEmpty())) {
            message = "Area must not be empty";
        }
        // Dieu 6: chua co ca nao vao ngay va buoi da chon
        if (message == null
                && dao.isSlotTaken(userID.trim(), shiftDate.trim(), shiftType)) {
            message = "This engineer already has a shift in that slot";
        }

        if (message == null) {
            String newID = dao.getNextID();
            if (dao.insert(new ShiftDto(newID, userID.trim(), shiftDate.trim(),
                    shiftType, area.trim()))) {
                request.setAttribute("MSG", "Shift " + newID + " created");
            } else {
                message = "Save failed";
            }
        }
        if (message != null) {
            request.setAttribute("ERROR", message);
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
        return "Shift schedule";
    }
}
