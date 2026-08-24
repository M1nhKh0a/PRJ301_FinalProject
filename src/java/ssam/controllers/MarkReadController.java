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
 * TODO 50. DA LAM. Danh dau mot thong bao la da doc.
 *
 * Phep chan quyen so huu duoc bao dam bang cach dua CA HAI cot vao menh de
 * dieu kien cua cau lenh cap nhat, xem TODO 28. Nho vay du nguoi dung go
 * thang dia chi voi ma thong bao cua nguoi khac thi cau lenh cung khong khop
 * dong nao va executeUpdate tra ve 0.
 */
@WebServlet(name = "MarkReadController", urlPatterns = {"/MarkReadController"})
public class MarkReadController extends HttpServlet {

    private static final String ERROR = "MainController?action=MyNotification";
    private static final String SUCCESS = "MainController?action=MyNotification";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = ERROR;
        try {
            HttpSession session = request.getSession(false);
            UserDto acc = (session == null) ? null : (UserDto) session.getAttribute("USER");

            String message = null;
            if (acc == null) {
                message = "Please login first";
                url = "login.jsp";
            } else {
                String id = request.getParameter("id");
                if (id == null || id.trim().isEmpty()) {
                    message = "Missing notification id";
                } else if (!new NotificationDao().markRead(id.trim(), acc.getUserID())) {
                    message = "Notification not found";
                }
                if (message == null) {
                    url = SUCCESS;
                }
            }
            if (message != null) {
                request.setAttribute("ERROR", message);
            }
        } catch (Exception e) {
            log("Error at MarkReadController: " + e.toString());
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
        return "Mark a notification as read";
    }
}
