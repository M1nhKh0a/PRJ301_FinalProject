package ssam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ssam.utils.DbUtils;

/**
 * Cac cau lenh SQL lam viec voi bang Anomalies.
 *
 * Ba phuong thuc DOC da duoc viet san lam mau, sau phuong thuc con lai la bai
 * tap. Doc ky ba phuong thuc mau truoc khi viet tiep.
 *
 * Ba dieu bat buoc giu: 1. Dung dau hoi lam cho trong, khong cong chuoi vao cau
 * lenh 2. Dong du ba tai nguyen trong khoi finally 3. Khong tim thay thi tra ve
 * danh sach rong, khong tra ve null
 */
public class AnomalyDao {

    /**
     * Cau lenh doc danh sach, day la cau phuc tap nhat cua ca bai.
     *
     * Ba phep noi bang: JOIN Equipment lay ten thiet bi va khu vuc JOIN Users u
     * lay ho ten nguoi bao, cot nay khong the null LEFT JOIN Users a lay ho ten
     * ky su, cot assignedTo CO THE null
     *
     * Dung JOIN thuong cho lan noi thu ba se lam bien mat moi ban ghi chua giao
     * viec, ma do lai chinh la nhung ban ghi can xu ly gap nhat.
     *
     * Nam dieu kien loc deu co the de trong, dung meo chon gia tri mac dinh sao
     * cho dieu kien tuong ung luon dung.
     *
     * Menh de sap xep dung CASE doi bon muc do thanh so, vi neu sap theo chu
     * cai thi Critical nam sau High va ban ghi gap khong len dau bang.
     */
    private static final String SEARCH
            = "SELECT n.anomalyID, n.equipmentID, n.reportedBy, n.reportedAt, "
            + "       n.severity, n.description, n.status, n.assignedTo, "
            + "       n.rootCause, n.resolvedAt, "
            + "       e.equipmentName, e.area, "
            + "       u.fullName AS reporterName, "
            + "       a.fullName AS assigneeName "
            + "FROM Anomalies n "
            + "     JOIN Equipment e ON n.equipmentID = e.equipmentID "
            + "     JOIN Users u     ON n.reportedBy  = u.userID "
            + "     LEFT JOIN Users a ON n.assignedTo = a.userID "
            + "WHERE (n.description LIKE ? OR n.equipmentID LIKE ?) "
            + "  AND (? = '' OR e.area = ?) "
            + "  AND (? = '' OR n.severity = ?) "
            + "  AND (? = '' OR n.status = ?) "
            + "  AND (? = '' OR n.assignedTo = ?) "
            + "ORDER BY CASE n.severity WHEN N'Critical' THEN 1 "
            + "                         WHEN N'High'     THEN 2 "
            + "                         WHEN N'Medium'   THEN 3 "
            + "                         ELSE 4 END, "
            + "         n.reportedAt DESC";

    /**
     * Cau lenh doc mot ban ghi. Ngoai ba phep noi bang giong tren, cau nay con
     * dem so lan kiem tra va so lan ket qua Pass bang hai cau truy van con,
     * dung khi kiem tra dieu kien dong ban ghi muc Critical.
     */
    private static final String GET_BY_ID
            = "SELECT n.anomalyID, n.equipmentID, n.reportedBy, n.reportedAt, "
            + "       n.severity, n.description, n.status, n.assignedTo, "
            + "       n.rootCause, n.resolvedAt, "
            + "       e.equipmentName, e.area, "
            + "       u.fullName AS reporterName, "
            + "       a.fullName AS assigneeName, "
            + "       (SELECT COUNT(*) FROM Inspections i "
            + "        WHERE i.anomalyID = n.anomalyID) AS inspectionCount, "
            + "       (SELECT COUNT(*) FROM Inspections i "
            + "        WHERE i.anomalyID = n.anomalyID AND i.result = N'Pass') AS passCount "
            + "FROM Anomalies n "
            + "     JOIN Equipment e ON n.equipmentID = e.equipmentID "
            + "     JOIN Users u     ON n.reportedBy  = u.userID "
            + "     LEFT JOIN Users a ON n.assignedTo = a.userID "
            + "WHERE n.anomalyID = ?";

    private static final String NEXT_ID
            = "SELECT MAX(CAST(SUBSTRING(anomalyID, 3, 10) AS INT)) AS maxNo "
            + "FROM Anomalies";

    /**
     * Doc mot dong ket qua thanh doi tuong, dung chung cho hai cau lenh doc.
     */
    private AnomalyDto read(ResultSet rs) throws SQLException {
        AnomalyDto n = new AnomalyDto(
                rs.getString("anomalyID"),
                rs.getString("equipmentID"),
                rs.getString("reportedBy"),
                rs.getString("reportedAt"),
                rs.getString("severity"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getString("assignedTo"),
                rs.getString("rootCause"),
                rs.getString("resolvedAt"));
        // Bon truong duoi day khong phai cot cua bang Anomalies
        n.setEquipmentName(rs.getString("equipmentName"));
        n.setArea(rs.getString("area"));
        n.setReporterName(rs.getString("reporterName"));
        n.setAssigneeName(rs.getString("assigneeName"));
        return n;
    }

    /**
     * TODO 16. DA LAM. Tim ban ghi theo nam dieu kien cung luc.
     */
    public List<AnomalyDto> search(String keyword, String area, String severity,
            String status, String assignedTo)
            throws SQLException, ClassNotFoundException {
        List<AnomalyDto> list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(SEARCH);
                st.setString(1, "%" + keyword + "%");
                st.setString(2, "%" + keyword + "%");
                st.setString(3, area);
                st.setString(4, area);
                st.setString(5, severity);
                st.setString(6, severity);
                st.setString(7, status);
                st.setString(8, status);
                st.setString(9, assignedTo);
                st.setString(10, assignedTo);
                rs = st.executeQuery();
                while (rs.next()) {
                    list.add(read(rs));
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
     * TODO 17. DA LAM. Doc mot ban ghi theo ma, kem so lan kiem tra.
     */
    public AnomalyDto getByID(String anomalyID)
            throws SQLException, ClassNotFoundException {
        AnomalyDto n = null;
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(GET_BY_ID);
                st.setString(1, anomalyID);
                rs = st.executeQuery();
                if (rs.next()) {
                    n = read(rs);
                    n.setInspectionCount(rs.getInt("inspectionCount"));
                    n.setPassCount(rs.getInt("passCount"));
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
        return n;
    }

    /**
     * TODO 24. DA LAM. Sinh ma ban ghi ke tiep.
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
        return String.format("AN%04d", next);
    }

    /**
     * TODO 18. Them mot ban ghi bat thuong moi. Thoi diem bao lay theo hien
     * tai, trang thai khoi tao la New, ba cot assignedTo, rootCause va
     * resolvedAt de trong.
     */
    public boolean insert(AnomalyDto a)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "INSERT INTO Anomalies (anomalyID, equipmentID, reportedBy, reportedAt, severity, description, status) "
                + "VALUES (?, ?, ?, GETDATE(), ?, ?, N'New')";
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, a.getAnomalyID());
                st.setString(2, a.getEquipmentID());
                st.setString(3, a.getReportedBy());
                st.setString(4, a.getSeverity());
                st.setString(5, a.getDescription());
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

    /**
     * TODO 19. Giao viec cho mot ky su, dat cot assignedTo va doi trang thai
     * sang Assigned. Mot cau lenh dat dong thoi hai cot, khong viet thanh hai
     * lenh rieng.
     */
    public boolean assign(String anomalyID, String engineerID)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "UPDATE Anomalies SET assignedTo = ?, status = N'Assigned' WHERE anomalyID = ?";
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, engineerID);
                st.setString(2, anomalyID);
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

    /**
     * TODO 20. Ky su ghi nguyen nhan goc va doi trang thai sang Resolved. Cot
     * resolvedAt lay theo thoi diem hien tai. Van la mot cau lenh duy nhat.
     */
    public boolean resolve(String anomalyID, String rootCause)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "UPDATE Anomalies SET rootCause = ?, status = N'Resolved', resolvedAt = GETDATE() WHERE anomalyID = ?";
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, rootCause);
                st.setString(2, anomalyID);
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

    /**
     * TODO 21. Doi trang thai, dung cho QA khi duyet dat hoac tu choi. Duyet
     * dat thi truyen Closed, tu choi thi truyen Rejected.
     */
    public boolean changeStatus(String anomalyID, String status)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "UPDATE Anomalies SET status = ? WHERE anomalyID = ?";
        Connection cn = null;
        PreparedStatement st = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, status);
                st.setString(2, anomalyID);
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

    /**
     * TODO 22. Dem so ban ghi theo mot trang thai, dung cho man hinh tong quan.
     */
    public int countByStatus(String status)
            throws SQLException, ClassNotFoundException {
        // TODO viet phan than o day
        String sql = "SELECT COUNT(*) AS total FROM Anomalies WHERE status = ?";
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                st.setString(1, status);
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
     * TODO 23. Thong ke so ban ghi theo tung muc do nghiem trong. Dung GROUP BY
     * tren cot severity, dat ten muc do vao truong severity va so luong vao
     * truong inspectionCount cua doi tuong tra ve. PHAI dung ham gop trong cau
     * lenh, khong duoc lay het ve roi dem trong Java.
     */
    public List<AnomalyDto> countBySeverity()
            throws SQLException, ClassNotFoundException {
        List<AnomalyDto> list = new ArrayList<>();
        // TODO viet phan than o day
        String sql = "SELECT severity, COUNT(*) AS total FROM Anomalies GROUP BY severity";
        Connection cn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = DbUtils.getConnection();
            if (cn != null) {
                st = cn.prepareStatement(sql);
                rs = st.executeQuery();
                while (rs.next()) {
                    AnomalyDto dto = new AnomalyDto();
                    dto.setSeverity(rs.getString("severity"));
                    dto.setInspectionCount(rs.getInt("total")); // Dung truong inspectionCount de chia so luong
                    list.add(dto);
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
}
