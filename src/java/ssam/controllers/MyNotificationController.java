package ssam.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.NotificationDao;
import ssam.model.UserDto;

/**
 * TODO 49. DA LAM. Danh sach thong bao cua nguoi dang dang nhap.
 *
 * Ma nguoi nhan lay tu PHIEN LAM VIEC chu khong bao gio lay tu tham so,
 * vi lay tu tham so thi ai cung sua duoc dia chi de xem thong bao nguoi khac.
 */
@WebServlet(name = "MyNotificationController", urlPatterns = {"/MyNotificationController"})
public class MyNotificationController extends HttpServlet {

    private static final String ERROR = "login.jsp";
    private static final String SUCCESS = "notification.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = ERROR;
        try {
            HttpSession session = request.getSession(false);
            UserDto acc = (session == null) ? null : (UserDto) session.getAttribute("USER");

            if (acc == null) {
                request.setAttribute("ERROR", "Please login first");
            } else {
                String filter = request.getParameter("filter");
                boolean unreadOnly = "unread".equals(filter);

                NotificationDao dao = new NotificationDao();
                request.setAttribute("LIST_NOTIFICATION",
                        dao.getByUser(acc.getUserID(), unreadOnly));
                request.setAttribute("filter", filter == null ? "" : filter);
                request.setAttribute("UNREAD", dao.countUnread(acc.getUserID()));
                url = SUCCESS;
            }
        } catch (Exception e) {
            log("Error at MyNotificationController: " + e.toString());
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
        return "My notifications";
    }
}
