package ssam.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ssam.model.EquipmentDao;
import ssam.model.EquipmentDto;
import ssam.model.NotificationDao;
import ssam.model.UserDto;

/**
 * TODO 51. DA LAM. Lo ba chang cua phan quan ly thiet bi.
 *
 * MAU THAM CHIEU cho servlet GHI du lieu. Ba diem dang hoc:
 *
 * 1. Mot servlet lo nhieu chang, phan biet bang chinh tham so action.
 *    Cach nay gon hon viec viet ba servlet rieng va khop voi cau truc dieu phoi.
 *
 * 2. Moi cau bao loi gom vao mot bien ten message, chi khi bien do van con
 *    null thi moi di tiep sang chang sau. Nho vay chi phai nhin mot bien duy
 *    nhat o moi chang, va tranh duoc cai bay chuyen tiep hai lan.
 *
 * 3. Khi thanh cong thi tro ve SearchEquipment chu khong tro thang toi trang
 *    jsp, vi trang khong tu truy van duoc nen di thang thi bang hien ra trong.
 */
@WebServlet(name = "EquipmentController", urlPatterns = {"/EquipmentController"})
public class EquipmentController extends HttpServlet {

    private static final String LIST_PAGE = "equipmentList.jsp";
    private static final String SEARCH = "MainController?action=SearchEquipment";

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
                String action = request.getParameter("action");
                EquipmentDao dao = new EquipmentDao();

                if ("SaveEquipment".equals(action)) {
                    url = handleSave(request, dao);
                } else {
                    if ("LoadEquipment".equals(action)) {
                        String id = request.getParameter("equipmentID");
                        if (id != null && !id.trim().isEmpty()) {
                            request.setAttribute("EQUIPMENT", dao.getByID(id.trim()));
                        }
                    }
                    loadList(request, dao);
                }
                request.setAttribute("UNREAD",
                        new NotificationDao().countUnread(acc.getUserID()));
            }
        } catch (Exception e) {
            log("Error at EquipmentController: " + e.toString());
            request.setAttribute("ERROR", "System error, please try again");
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /** Nap danh sach kem ba gia tri loc, dung chung cho moi chang. */
    private void loadList(HttpServletRequest request, EquipmentDao dao) throws Exception {
        String name = request.getParameter("searchName");
        String area = request.getParameter("searchArea");
        String status = request.getParameter("searchStatus");
        if (name == null) { name = ""; }
        if (area == null) { area = ""; }
        if (status == null) { status = ""; }

        request.setAttribute("LIST_EQUIPMENT", dao.search(name.trim(), area, status));
        request.setAttribute("searchName", name);
        request.setAttribute("searchArea", area);
        request.setAttribute("searchStatus", status);
    }

    /** Chang ghi: kiem tra sau dieu roi them hoac cap nhat. */
    private String handleSave(HttpServletRequest request, EquipmentDao dao) throws Exception {
        String id = request.getParameter("equipmentID");
        String name = request.getParameter("equipmentName");
        String area = request.getParameter("area");
        String model = request.getParameter("model");

        boolean isNew = (id == null || id.trim().isEmpty());
        String message = null;

        // Dieu 2 va 3: ten khong trong, khong qua dai, chua bi dung
        if (name == null || name.trim().isEmpty()) {
            message = "Equipment name must not be empty";
        } else if (name.trim().length() > 100) {
            message = "Equipment name must not exceed 100 characters";
        } else if (dao.isNameExisted(name.trim(), isNew ? "" : id.trim())) {
            message = "Equipment name already exists";
        }
        // Dieu 4 va 5
        if (message == null && (area == null || area.trim().isEmpty())) {
            message = "Area must not be empty";
        }
        if (message == null && (model == null || model.trim().isEmpty())) {
            message = "Model must not be empty";
        }
        // Dieu 6: thiet bi da ngung dung thi khong sua nua
        EquipmentDto old = null;
        if (message == null && !isNew) {
            old = dao.getByID(id.trim());
            if (old == null) {
                message = "Equipment not found";
            } else if ("Retired".equals(old.getStatus())) {
                message = "A retired equipment cannot be edited";
            }
        }

        if (message == null) {
            boolean done;
            if (isNew) {
                String newID = dao.getNextID();
                done = dao.insert(new EquipmentDto(newID, name.trim(),
                        area.trim(), model.trim(), "Running"));
            } else {
                done = dao.update(new EquipmentDto(id.trim(), name.trim(),
                        area.trim(), model.trim(), old.getStatus()));
            }
            if (done) {
                request.setAttribute("MSG", isNew ? "Equipment created" : "Equipment updated");
                loadList(request, dao);
                return LIST_PAGE;
            }
            message = "Save failed";
        }

        // Con loi thi bao va do lai du lieu vua go
        request.setAttribute("ERROR", message);
        request.setAttribute("EQUIPMENT", new EquipmentDto(
                id == null ? "" : id,
                name == null ? "" : name,
                area == null ? "" : area,
                model == null ? "" : model,
                old == null ? "Running" : old.getStatus()));
        loadList(request, dao);
        return LIST_PAGE;
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
        return "Equipment management";
    }
}
