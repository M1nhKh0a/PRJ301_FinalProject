package ssam.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ssam.model.UserDao;
import ssam.model.UserDto;

/**
 * TODO 39. DA LAM. Dang nhap voi bon nhanh.
 *
 * Nhanh thu ba kiem tra cot status va phai dat SAU khi da xac thuc dung tai
 * khoan. Tai khoan en04 trong du lieu mau dang bi khoa, dung de thu nhanh nay.
 *
 * Toan bo servlet chi co MOT lenh chuyen tiep, dat o khoi finally.
 */
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    private static final String ERROR = "login.jsp";
    private static final String SUCCESS = "MainController?action=Dashboard";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String url = ERROR;
        try {
            String userID = request.getParameter("userID");
            String password = request.getParameter("password");

            if (userID == null || userID.trim().isEmpty()
                    || password == null || password.isEmpty()) {
                request.setAttribute("ERROR", "Please enter user and password");
            } else {
                UserDao dao = new UserDao();
                UserDto dto = dao.checkLogin(userID.trim(), password);

                if (dto == null) {
                    request.setAttribute("ERROR", "Invalid user or password");
                } else if (!dto.isStatus()) {
                    request.setAttribute("ERROR", "This account is disabled");
                } else {
                    request.getSession().setAttribute("USER", dto);
                    url = SUCCESS;
                }
            }
        } catch (Exception e) {
            log("Error at LoginController: " + e.toString());
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
        return "Login";
    }
}
