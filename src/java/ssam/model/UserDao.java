package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Users.
 * DA VIET DAY DU, dung lam mau tham chieu cho cac lop Dao khac.
 *
 * Ba dieu bat buoc giu khi chep khuon:
 *   1. Dung dau hoi lam cho trong, khong cong chuoi vao cau lenh
 *   2. Dong du ba tai nguyen trong khoi finally, theo thu tu nguoc voi luc mo
 *   3. Khong tim thay thi tra ve danh sach rong, khong tra ve null
 */
public class UserDao {

    private static final String CHECK_LOGIN
            = "SELECT userID, fullName, email, password, roleID, area, status "
            + "FROM Users WHERE userID = ? AND password = ?";

    private static final String GET_BY_ID
            = "SELECT userID, fullName, email, password, roleID, area, status "
            + "FROM Users WHERE userID = ?";

    private static final String GET_BY_ROLE
            = "SELECT userID, fullName, email, password, roleID, area, status "
            + "FROM Users WHERE roleID = ? AND status = 1 ORDER BY fullName";

    /** Doc mot dong tu ket qua truy van thanh doi tuong. */
    private UserDto read(ResultSet rs) throws SQLException {
        return new UserDto(
                rs.getString("userID"),
                rs.getString("fullName"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("roleID"),
                rs.getString("area"),
                rs.getBoolean("status"));
    }

    /**
     * TODO 1. DA LAM. Kiem tra dang nhap.
     * Phuong thuc chi tra ve du lieu doc duoc, viec kiem tra cot status
     * thuoc ve tang dieu khien.
     */
    public UserDto checkLogin(String userID, String password)
            throws SQLException, ClassNotFoundException {
        UserDto u = null;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(CHECK_LOGIN);
                st.setString(1, userID);
                st.setString(2, password);
                rs = st.executeQuery();
                if (rs.next()) {
                    u = read(rs);
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return u;
    }

    /** TODO 2. DA LAM. Doc mot tai khoan theo ma. */
    public UserDto getByID(String userID)
            throws SQLException, ClassNotFoundException {
        UserDto u = null;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(GET_BY_ID);
                st.setString(1, userID);
                rs = st.executeQuery();
                if (rs.next()) {
                    u = read(rs);
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return u;
    }

    /**
     * TODO 3. DA LAM. Danh sach tai khoan theo vai tro, chi lay tai khoan
     * dang hoat dong. Dung khi gui thong bao cho toan bo QA hoac toan bo QC.
     */
    public List<UserDto> getByRole(String roleID)
            throws SQLException, ClassNotFoundException {
        List<UserDto> list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(GET_BY_ROLE);
                st.setString(1, roleID);
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
}
