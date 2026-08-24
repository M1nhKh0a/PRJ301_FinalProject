package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Ban ghi bat thuong.
 * Vong doi trang thai: New, Assigned, Resolved, Closed, Rejected.
 * Hai truong inspectionCount va passCount dung khi kiem tra dieu kien dong
 * ban ghi muc Critical, tinh bang cau truy van con dem tren bang Inspections.
 */
public class AnomalyDto {

    private String anomalyID;
    private String equipmentID;
    private String reportedBy;
    private String reportedAt;
    private String severity;
    private String description;
    private String status;
    private String assignedTo;
    private String rootCause;
    private String resolvedAt;

    // Cac truong duoi day KHONG phai cot cua bang,
    // chung lay ve nho phep noi bang trong cau lenh truy van.
    private String equipmentName;
    private String area;
    private String reporterName;
    private String assigneeName;
    private int inspectionCount;
    private int passCount;

    public AnomalyDto() {
    }

    public AnomalyDto(String anomalyID, String equipmentID, String reportedBy, String reportedAt, String severity, String description, String status, String assignedTo, String rootCause, String resolvedAt) {
        this.anomalyID = anomalyID;
        this.equipmentID = equipmentID;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
        this.severity = severity;
        this.description = description;
        this.status = status;
        this.assignedTo = assignedTo;
        this.rootCause = rootCause;
        this.resolvedAt = resolvedAt;
    }

    public String getAnomalyID() { return anomalyID; }
    public void setAnomalyID(String anomalyID) { this.anomalyID = anomalyID; }
    public String getEquipmentID() { return equipmentID; }
    public void setEquipmentID(String equipmentID) { this.equipmentID = equipmentID; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public String getReportedAt() { return reportedAt; }
    public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public String getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(String resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public int getInspectionCount() { return inspectionCount; }
    public void setInspectionCount(int inspectionCount) { this.inspectionCount = inspectionCount; }
    public int getPassCount() { return passCount; }
    public void setPassCount(int passCount) { this.passCount = passCount; }
}
