package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Reviews. Hai phuong thuc DOC da viet san,
 * phuong thuc GHI la bai tap.
 */
public class ReviewDao {

    private static final String GET_BY_ANOMALY
            = "SELECT r.reviewID, r.anomalyID, r.reviewedBy, r.decision, "
            + "       r.comment, r.reviewedAt, u.fullName AS reviewerName "
            + "FROM Reviews r JOIN Users u ON r.reviewedBy = u.userID "
            + "WHERE r.anomalyID = ? "
            + "ORDER BY r.reviewedAt DESC";

    private static final String NEXT_ID
            = "SELECT MAX(CAST(SUBSTRING(reviewID, 3, 10) AS INT)) AS maxNo FROM Reviews";

    /**
     * TODO 30. DA LAM. Lich su duyet cua mot ban ghi, kem ho ten nguoi duyet.
     */
    public List<ReviewDto> getByAnomaly(String anomalyID)
            throws SQLException, ClassNotFoundException {
        List<ReviewDto> list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(GET_BY_ANOMALY);
                st.setString(1, anomalyID);
                rs = st.executeQuery();
                while (rs.next()) {
                    ReviewDto r = new ReviewDto(
                            rs.getString("reviewID"),
                            rs.getString("anomalyID"),
                            rs.getString("reviewedBy"),
                            rs.getString("decision"),
                            rs.getString("comment"),
                            rs.getString("reviewedAt"));
                    r.setReviewerName(rs.getString("reviewerName"));
                    list.add(r);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (cn != null) {
                cn.close();
            }
        }
        return list;
    }

    /**
     * TODO 32. DA LAM. Sinh ma lan duyet ke tiep.
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
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (cn != null) {
                cn.close();
            }
        }
        return String.format("RV%04d", next);
    }

    /**
     * TODO 31. Them mot lan duyet. Thoi diem duyet lay theo hien tai.
     */
    public boolean insert(ReviewDto r)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "INSERT INTO Reviews (reviewID, anomalyID, reviewedBy, decision, comment, reviewedAt) "
                + "VALUES (?, ?, ?, ?, ?, GETDATE())";
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, r.getReviewID());
                st.setString(2, r.getAnomalyID());
                st.setString(3, r.getReviewedBy());
                st.setString(4, r.getDecision());
                st.setString(5, r.getComment());
                return st.executeUpdate() > 0;
            }
        } finally {
            if (st != null) {
                st.close();
            }
            if (cn != null) {
                cn.close();
            }
        }
        return false;
    }
}
