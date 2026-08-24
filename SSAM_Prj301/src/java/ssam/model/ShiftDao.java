package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Shifts.
 * DA VIET DAY DU. Bang nay nuoi du lieu cho co che giao viec tu dong,
 * nen sai o day thi ca chuoi phia sau chay sai theo.
 */
public class ShiftDao {

    private static final String SEARCH_BY_DATE
            = "SELECT s.shiftID, s.userID, s.shiftDate, s.shiftType, s.area, "
            + "       u.fullName "
            + "FROM Shifts s JOIN Users u ON s.userID = u.userID "
            + "WHERE s.shiftDate >= ? AND s.shiftDate <= ? "
            + "ORDER BY s.shiftDate DESC, s.shiftType, s.area";

    /**
     * Tim ky su dang truc ca. Ba dieu kien phai khop dong thoi, va chi lay
     * ky su co cot status bang 1, vi giao viec cho tai khoan da khoa thi
     * nguoi do khong dang nhap vao xu ly duoc.
     */
    private static final String FIND_ON_DUTY
            = "SELECT s.shiftID, s.userID, s.shiftDate, s.shiftType, s.area, "
            + "       u.fullName "
            + "FROM Shifts s JOIN Users u ON s.userID = u.userID "
            + "WHERE s.area = ? AND s.shiftDate = ? AND s.shiftType = ? "
            + "  AND u.roleID = 'EN' AND u.status = 1";

    private static final String INSERT
            = "INSERT INTO Shifts (shiftID, userID, shiftDate, shiftType, area) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String CHECK_SLOT
            = "SELECT shiftID FROM Shifts "
            + "WHERE userID = ? AND shiftDate = ? AND shiftType = ?";

    private static final String NEXT_ID
            = "SELECT MAX(CAST(SUBSTRING(shiftID, 3, 10) AS INT)) AS maxNo FROM Shifts";

    private ShiftDto read(ResultSet rs) throws SQLException {
        ShiftDto s = new ShiftDto(
                rs.getString("shiftID"),
                rs.getString("userID"),
                rs.getString("shiftDate"),
                rs.getString("shiftType"),
                rs.getString("area"));
        s.setFullName(rs.getString("fullName"));   // lay tu phep noi bang
        return s;
    }

    /** TODO 11. DA LAM. Danh sach ca truc trong mot khoang ngay. */
    public List<ShiftDto> searchByDate(String fromDate, String toDate)
            throws SQLException, ClassNotFoundException {
        List<ShiftDto> list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(SEARCH_BY_DATE);
                st.setString(1, fromDate);
                st.setString(2, toDate);
                rs = st.executeQuery();
                while (rs.next()) {
                    list.add(read(rs));
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return list;
    }

    /**
     * TODO 12. DA LAM. Tim ky su dang truc ca.
     * Ket qua co the null khi khong ai truc, luc do ban ghi giu trang thai New.
     */
    public ShiftDto findOnDuty(String area, String shiftDate, String shiftType)
            throws SQLException, ClassNotFoundException {
        ShiftDto s = null;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(FIND_ON_DUTY);
                st.setString(1, area);
                st.setString(2, shiftDate);
                st.setString(3, shiftType);
                rs = st.executeQuery();
                if (rs.next()) {
                    s = read(rs);
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return s;
    }

    /** TODO 13. DA LAM. Them mot ca truc. */
    public boolean insert(ShiftDto s)
            throws SQLException, ClassNotFoundException {
        boolean done = false;
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(INSERT);
                st.setString(1, s.getShiftID());
                st.setString(2, s.getUserID());
                st.setString(3, s.getShiftDate());
                st.setString(4, s.getShiftType());
                st.setString(5, s.getArea());
                done = st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return done;
    }

    /** TODO 14. DA LAM. Kiem tra mot nguoi da co ca truc vao ngay va buoi do chua. */
    public boolean isSlotTaken(String userID, String shiftDate, String shiftType)
            throws SQLException, ClassNotFoundException {
        boolean taken = false;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(CHECK_SLOT);
                st.setString(1, userID);
                st.setString(2, shiftDate);
                st.setString(3, shiftType);
                rs = st.executeQuery();
                taken = rs.next();
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return taken;
    }

    /** TODO 15. DA LAM. Sinh ma ca truc ke tiep. */
    public String getNextID()
            throws SQLException, ClassNotFoundException {
        int next = 1;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(NEXT_ID);
                rs = st.executeQuery();
                if (rs.next()) {
                    next = rs.getInt("maxNo") + 1;
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return String.format("SH%04d", next);
    }
}
