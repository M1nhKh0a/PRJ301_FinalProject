package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Notifications.
 * DA VIET DAY DU.
 */
public class NotificationDao {

    /**
     * Noi voi bang Anomalies de lay muc do nghiem trong va trang thai hien tai
     * cua ban ghi lien quan, nho vay nguoi nhan biet viec con gap hay da xong
     * ma khong phai mo tung ban ghi ra xem.
     *
     * Dieu kien loc theo trang thai da doc dung meo mot cau lenh cho hai truong
     * hop: truyen 0 thi lay tat ca, truyen 1 thi chi lay dong chua doc.
     */
    private static final String GET_BY_USER
            = "SELECT t.notificationID, t.anomalyID, t.userID, t.message, "
            + "       t.createdAt, t.isRead, "
            + "       n.severity, n.status AS anomalyStatus "
            + "FROM Notifications t JOIN Anomalies n ON t.anomalyID = n.anomalyID "
            + "WHERE t.userID = ? AND (? = 0 OR t.isRead = 0) "
            + "ORDER BY t.createdAt DESC";

    private static final String COUNT_UNREAD
            = "SELECT COUNT(*) AS total FROM Notifications "
            + "WHERE userID = ? AND isRead = 0";

    private static final String INSERT
            = "INSERT INTO Notifications "
            + "(notificationID, anomalyID, userID, message, createdAt, isRead) "
            + "VALUES (?, ?, ?, ?, GETDATE(), 0)";

    /**
     * Menh de dieu kien co CA HAI cot. Neu chi loc theo ma thong bao thi
     * mot nguoi danh dau duoc thong bao cua nguoi khac.
     */
    private static final String MARK_READ
            = "UPDATE Notifications SET isRead = 1 "
            + "WHERE notificationID = ? AND userID = ?";

    private static final String NEXT_ID
            = "SELECT MAX(CAST(SUBSTRING(notificationID, 3, 10) AS INT)) AS maxNo "
            + "FROM Notifications";

    /** TODO 25. DA LAM. Danh sach thong bao cua mot nguoi. */
    public List<NotificationDto> getByUser(String userID, boolean unreadOnly)
            throws SQLException, ClassNotFoundException {
        List<NotificationDto> list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(GET_BY_USER);
                st.setString(1, userID);
                st.setInt(2, unreadOnly ? 1 : 0);
                rs = st.executeQuery();
                while (rs.next()) {
                    NotificationDto t = new NotificationDto(
                            rs.getString("notificationID"),
                            rs.getString("anomalyID"),
                            rs.getString("userID"),
                            rs.getString("message"),
                            rs.getString("createdAt"),
                            rs.getBoolean("isRead"));
                    t.setSeverity(rs.getString("severity"));
                    t.setAnomalyStatus(rs.getString("anomalyStatus"));
                    list.add(t);
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return list;
    }

    /** TODO 26. DA LAM. Dem so thong bao chua doc. */
    public int countUnread(String userID)
            throws SQLException, ClassNotFoundException {
        int total = 0;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(COUNT_UNREAD);
                st.setString(1, userID);
                rs = st.executeQuery();
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } finally {
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return total;
    }

    /** TODO 27. DA LAM. Them mot thong bao. */
    public boolean insert(NotificationDto t)
            throws SQLException, ClassNotFoundException {
        boolean done = false;
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(INSERT);
                st.setString(1, t.getNotificationID());
                st.setString(2, t.getAnomalyID());
                st.setString(3, t.getUserID());
                st.setString(4, t.getMessage());
                done = st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return done;
    }

    /** TODO 28. DA LAM. Danh dau mot thong bao la da doc. */
    public boolean markRead(String notificationID, String userID)
            throws SQLException, ClassNotFoundException {
        boolean done = false;
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(MARK_READ);
                st.setString(1, notificationID);
                st.setString(2, userID);
                done = st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) { st.close(); }
            if (cn != null) { cn.close(); }
        }
        return done;
    }

    /** TODO 29. DA LAM. Sinh ma thong bao ke tiep. */
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
        return String.format("NT%04d", next);
    }
}
