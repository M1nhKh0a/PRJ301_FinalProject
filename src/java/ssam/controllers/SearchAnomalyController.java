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
 * TODO 42. DA LAM. Tim ban ghi bat thuong theo nam dieu kien.
 *
 * MAU THAM CHIEU cho servlet DOC du lieu. Bon viec theo dung thu tu:
 *   1. Lay nguoi dang nhap tu phien, kiem tra quyen
 *   2. Doc tham so, o van ban de trong thi doi thanh chuoi rong
 *   3. Goi Dao, dat ket qua vao request
 *   4. Dat lai ca nam gia tri vua chon de trang do nguoc vao cac o loc
 *
 * Viec thu tu la cho de bo sot nhat. Thieu no thi moi lan bam tim xong cac
 * o loc lai trang, nguoi dung muon sua mot dieu kien phai chon lai tu dau.
 */
@WebServlet(name = "SearchAnomalyController", urlPatterns = {"/SearchAnomalyController"})
public class SearchAnomalyController extends HttpServlet {

    private static final String ERROR = "login.jsp";
    private static final String SUCCESS = "anomalyList.jsp";

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
                String keyword = request.getParameter("keyword");
                String area = request.getParameter("area");
                String severity = request.getParameter("severity");
                String status = request.getParameter("status");
                String onlyMine = request.getParameter("onlyMine");

                if (keyword == null) { keyword = ""; }
                if (area == null) { area = ""; }
                if (severity == null) { severity = ""; }
                if (status == null) { status = ""; }

                // O danh dau chi co y nghia voi ky su
                String assignedTo = "";
                if (onlyMine != null && acc.isEngineer()) {
                    assignedTo = acc.getUserID();
                }

                AnomalyDao dao = new AnomalyDao();
                List<AnomalyDto> list = dao.search(keyword.trim(), area, severity,
                        status, assignedTo);

                request.setAttribute("LIST_ANOMALY", list);
                request.setAttribute("keyword", keyword);
                request.setAttribute("area", area);
                request.setAttribute("severity", severity);
                request.setAttribute("status", status);
                request.setAttribute("onlyMine", onlyMine == null ? "" : onlyMine);

                // Con so ben canh menu thong bao
                request.setAttribute("UNREAD",
                        new NotificationDao().countUnread(acc.getUserID()));
                url = SUCCESS;
            }
        } catch (Exception e) {
            log("Error at SearchAnomalyController: " + e.toString());
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
        return "Search anomalies";
    }
}
