package ssam.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 38. DA LAM. Bo dieu phoi trung tam.
 *
 * Ba dieu can nho:
 *   1. Kiem tra action khac null truoc khi vao khoi re nhanh, vi web.xml khai
 *      chinh lop nay lam trang mac dinh nen lan chay dau tien khong co tham so
 *   2. Hai nhanh LoadEquipment va SaveEquipment cung tro ve mot servlet,
 *      servlet do tu phan biet chang doc va chang ghi theo gia tri action
 *   3. Nhanh mac dinh dua ve trang dang nhap, khong nem ngoai le
 */
public class MainController extends HttpServlet {

    private static final String WELCOME = "login.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = WELCOME;
        try {
            String action = request.getParameter("action");
            if (action != null) {
                switch (action) {
                    case "Login":       url = "LoginController";       break;
                    case "Logout":      url = "LogoutController";      break;
                    case "Dashboard":   url = "DashboardController";   break;

                    case "SearchAnomaly": url = "SearchAnomalyController"; break;
                    case "ReportAnomaly": url = "ReportAnomalyController"; break;
                    case "LoadAnomaly":   url = "LoadAnomalyController";   break;

                    case "AssignAnomaly":  url = "AssignAnomalyController";  break;
                    case "ResolveAnomaly": url = "ResolveAnomalyController"; break;
                    case "ReviewAnomaly":  url = "ReviewAnomalyController";  break;
                    case "InspectAnomaly": url = "InspectAnomalyController"; break;

                    case "MyNotification": url = "MyNotificationController"; break;
                    case "MarkRead":       url = "MarkReadController";       break;

                    case "SearchEquipment":       url = "EquipmentController"; break;
                    case "LoadEquipment":         url = "EquipmentController"; break;
                    case "SaveEquipment":         url = "EquipmentController"; break;
                    case "ChangeEquipmentStatus": url = "ChangeEquipmentStatusController"; break;

                    case "SearchShift": url = "ShiftController"; break;
                    case "SaveShift":   url = "ShiftController"; break;

                    default: url = WELCOME;
                }
            }
        } catch (Exception e) {
            log("Error at MainController: " + e.toString());
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
        return "Front controller";
    }
}
