package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Equipment.
 * DA VIET DAY DU, dung lam mau cho phan quan ly danh muc.
 */
public class EquipmentDao {

    /**
     * Meo de mot cau lenh chay duoc cho moi to hop o trong:
     * chon gia tri mac dinh sao cho dieu kien tuong ung luon dung.
     *   o van ban de trong  truyen chuoi rong, ve dau cua ngoac don thanh dung
     *   o ten                truyen chuoi rong, nam giua hai dau phan tram
     */
    private static final String SEARCH
            = "SELECT equipmentID, equipmentName, area, model, status "
            + "FROM Equipment "
            + "WHERE equipmentName LIKE ? "
            + "  AND (? = '' OR area = ?) "
            + "  AND (? = '' OR status = ?) "
            + "ORDER BY equipmentID";

    private static final String GET_BY_ID
            = "SELECT equipmentID, equipmentName, area, model, status "
            + "FROM Equipment WHERE equipmentID = ?";

    private static final String CHECK_NAME
            = "SELECT equipmentID FROM Equipment "
            + "WHERE equipmentName = ? AND equipmentID <> ?";

    private static final String INSERT
            = "INSERT INTO Equipment (equipmentID, equipmentName, area, model, status) "
            + "VALUES (?, ?, ?, ?, N'Running')";

    private static final String UPDATE
            = "UPDATE Equipment SET equipmentName = ?, area = ?, model = ? "
            + "WHERE equipmentID = ?";

    private static final String CHANGE_STATUS
            = "UPDATE Equipment SET status = ? WHERE equipmentID = ?";

    private static final String NEXT_ID
            = "SELECT MAX(CAST(SUBSTRING(equipmentID, 3, 10) AS INT)) AS maxNo "
            + "FROM Equipment";

    private EquipmentDto read(ResultSet rs) throws SQLException {
        return new EquipmentDto(
                rs.getString("equipmentID"),
                rs.getString("equipmentName"),
                rs.getString("area"),
                rs.getString("model"),
                rs.getString("status"));
    }

    /** TODO 4. DA LAM. Tim thiet bi theo ba dieu kien cung luc. */
    public List<EquipmentDto> search(String name, String area, String status)
            throws SQLException, ClassNotFoundException {
        List<EquipmentDto> list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(SEARCH);
                st.setString(1, "%" + name + "%");
                st.setString(2, area);
                st.setString(3, area);
                st.setString(4, status);
                st.setString(5, status);
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

    /** TODO 5. DA LAM. Doc mot thiet bi theo ma. */
    public EquipmentDto getByID(String equipmentID)
            throws SQLException, ClassNotFoundException {
        EquipmentDto e = null;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(GET_BY_ID);
                st.setString(1, equipmentID);
                rs = st.executeQuery();
                if (rs.next()) {
                    e = read(rs);
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return e;
    }

    /**
     * TODO 6. DA LAM. Kiem tra ten thiet bi da duoc dung chua.
     * Tham so exceptID loai chinh thiet bi dang sua ra khoi phep kiem tra,
     * nho vay giu nguyen ten cu ma chi sua model thi van luu duoc.
     * Khi them moi thi truyen chuoi rong.
     */
    public boolean isNameExisted(String equipmentName, String exceptID)
            throws SQLException, ClassNotFoundException {
        boolean existed = false;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(CHECK_NAME);
                st.setString(1, equipmentName);
                st.setString(2, exceptID == null ? "" : exceptID);
                rs = st.executeQuery();
                existed = rs.next();
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return existed;
    }

    /** TODO 7. DA LAM. Them mot thiet bi moi, trang thai khoi tao la Running. */
    public boolean insert(EquipmentDto e)
            throws SQLException, ClassNotFoundException {
        boolean done = false;
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(INSERT);
                st.setString(1, e.getEquipmentID());
                st.setString(2, e.getEquipmentName());
                st.setString(3, e.getArea());
                st.setString(4, e.getModel());
                done = st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return done;
    }

    /**
     * TODO 8. DA LAM. Cap nhat ten, khu vuc va model.
     * CAI BAY: cot equipmentID nam o menh de dieu kien nen la dau hoi THU TU.
     */
    public boolean update(EquipmentDto e)
            throws SQLException, ClassNotFoundException {
        boolean done = false;
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(UPDATE);
                st.setString(1, e.getEquipmentName());
                st.setString(2, e.getArea());
                st.setString(3, e.getModel());
                st.setString(4, e.getEquipmentID());
                done = st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return done;
    }

    /** TODO 9. DA LAM. Doi trang thai thiet bi. */
    public boolean changeStatus(String equipmentID, String status)
            throws SQLException, ClassNotFoundException {
        boolean done = false;
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(CHANGE_STATUS);
                st.setString(1, status);
                st.setString(2, equipmentID);
                done = st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return done;
    }

    /**
     * TODO 10. DA LAM. Sinh ma thiet bi ke tiep.
     * Phai doi sang so truoc khi so sanh, neu so sanh theo chuoi thi
     * EQ009 se lon hon EQ010 va ma moi sinh ra bi trung.
     */
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
        return String.format("EQ%03d", next);
    }
}
