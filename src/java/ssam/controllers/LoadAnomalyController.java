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
import ssam.model.InspectionDao;
import ssam.model.NotificationDao;
import ssam.model.ReviewDao;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 44. DA LAM. Man hinh chi tiet mot ban ghi.
 *
 * Trang chi tiet la noi thuc hien ca bon thao tac giao viec, xu ly, duyet va
 * kiem tra, nen phai nap du du lieu cho ca bon:
 *   ANOMALY       ban ghi kem ten thiet bi va ten hai nguoi lien quan
 *   LIST_REVIEW   lich su duyet cua QA
 *   LIST_INSPECT  danh sach lan kiem tra cua QC
 *   LIST_ENGINEER danh sach ky su, chi nap khi nguoi xem la AD hoac EN
 */
@WebServlet(name = "LoadAnomalyController", urlPatterns = {"/LoadAnomalyController"})
public class LoadAnomalyController extends HttpServlet {

    private static final String ERROR = "MainController?action=SearchAnomaly";
    private static final String SUCCESS = "anomalyDetail.jsp";

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
                url = "login.jsp";
            } else {
                String id = request.getParameter("id");
                AnomalyDao dao = new AnomalyDao();
                AnomalyDto n = (id == null || id.trim().isEmpty())
                        ? null : dao.getByID(id.trim());

                if (n == null) {
                    request.setAttribute("ERROR", "Anomaly not found");
                } else {
                    request.setAttribute("ANOMALY", n);
                    request.setAttribute("LIST_REVIEW",
                            new ReviewDao().getByAnomaly(n.getAnomalyID()));
                    request.setAttribute("LIST_INSPECT",
                            new InspectionDao().getByAnomaly(n.getAnomalyID()));

                    if (acc.isAdmin() || acc.isEngineer()) {
                        List<UserDto> engineers = new UserDao().getByRole("EN");
                        request.setAttribute("LIST_ENGINEER", engineers);
                    }
                    request.setAttribute("UNREAD",
                            new NotificationDao().countUnread(acc.getUserID()));
                    url = SUCCESS;
                }
            }
        } catch (Exception e) {
            log("Error at LoadAnomalyController: " + e.toString());
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
        return "Anomaly detail";
    }
}
