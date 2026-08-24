package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Inspections. Hai phuong thuc da viet san,
 * ba phuong thuc con lai la bai tap.
 */
public class InspectionDao {

    private static final String GET_BY_ANOMALY
            = "SELECT i.inspectionID, i.anomalyID, i.inspectedBy, i.lotID, "
            + "       i.result, i.note, i.inspectedAt, u.fullName AS inspectorName "
            + "FROM Inspections i JOIN Users u ON i.inspectedBy = u.userID "
            + "WHERE i.anomalyID = ? "
            + "ORDER BY i.inspectedAt DESC";

    private static final String NEXT_ID
            = "SELECT MAX(CAST(SUBSTRING(inspectionID, 3, 10) AS INT)) AS maxNo "
            + "FROM Inspections";

    /**
     * TODO 33. DA LAM. Danh sach lan kiem tra cua mot ban ghi.
     */
    public List<InspectionDto> getByAnomaly(String anomalyID)
            throws SQLException, ClassNotFoundException {
        List<InspectionDto> list = new ArrayList<>();
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
                    InspectionDto i = new InspectionDto(
                            rs.getString("inspectionID"),
                            rs.getString("anomalyID"),
                            rs.getString("inspectedBy"),
                            rs.getString("lotID"),
                            rs.getString("result"),
                            rs.getString("note"),
                            rs.getString("inspectedAt"));
                    i.setInspectorName(rs.getString("inspectorName"));
                    list.add(i);
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
     * TODO 37. DA LAM. Sinh ma lan kiem tra ke tiep.
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
        return String.format("IS%04d", next);
    }

    /**
     * TODO 34. Kiem tra mot lo hang da duoc ghi nhan cho ban ghi nay chua. Rang
     * buoc UQ_Ins_AnoLot cung chan viec nay, nhung tang dieu khien phai kiem
     * tra truoc de bao bang cau de doc.
     */
    public boolean isLotInspected(String anomalyID, String lotID)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "SELECT inspectionID FROM Inspections WHERE anomalyID = ? AND lotID = ?";
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, anomalyID);
                st.setString(2, lotID);
                rs = st.executeQuery();
                if (rs.next()) {
                    return true;
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
        return false;
    }

    /**
     * TODO 35. Dem so lan kiem tra co ket qua Pass. Dung khi kiem tra dieu kien
     * dong ban ghi muc Critical.
     */
    public int countPass(String anomalyID)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "SELECT COUNT(*) AS total FROM Inspections WHERE anomalyID = ? AND result = N'Pass'";
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, anomalyID);
                rs = st.executeQuery();
                if (rs.next()) {
                    return rs.getInt("total");
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
        return 0;
    }

    /**
     * TODO 36. Them mot lan kiem tra. Thoi diem kiem tra lay theo hien tai.
     */
    public boolean insert(InspectionDto i)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "INSERT INTO Inspections (inspectionID, anomalyID, inspectedBy, lotID, result, note, inspectedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, i.getInspectionID());
                st.setString(2, i.getAnomalyID());
                st.setString(3, i.getInspectedBy());
                st.setString(4, i.getLotID());
                st.setString(5, i.getResult());
                st.setString(6, i.getNote());
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
