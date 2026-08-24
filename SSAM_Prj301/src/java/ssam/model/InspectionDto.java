package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Ket qua kiem tra lo hang cua QC. Cot result nhan Pass, Fail hoac Rework.
 */
public class InspectionDto {

    private String inspectionID;
    private String anomalyID;
    private String inspectedBy;
    private String lotID;
    private String result;
    private String note;
    private String inspectedAt;

    // Cac truong duoi day KHONG phai cot cua bang,
    // chung lay ve nho phep noi bang trong cau lenh truy van.
    private String inspectorName;

    public InspectionDto() {
    }

    public InspectionDto(String inspectionID, String anomalyID, String inspectedBy, String lotID, String result, String note, String inspectedAt) {
        this.inspectionID = inspectionID;
        this.anomalyID = anomalyID;
        this.inspectedBy = inspectedBy;
        this.lotID = lotID;
        this.result = result;
        this.note = note;
        this.inspectedAt = inspectedAt;
    }

    public String getInspectionID() { return inspectionID; }
    public void setInspectionID(String inspectionID) { this.inspectionID = inspectionID; }
    public String getAnomalyID() { return anomalyID; }
    public void setAnomalyID(String anomalyID) { this.anomalyID = anomalyID; }
    public String getInspectedBy() { return inspectedBy; }
    public void setInspectedBy(String inspectedBy) { this.inspectedBy = inspectedBy; }
    public String getLotID() { return lotID; }
    public void setLotID(String lotID) { this.lotID = lotID; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getInspectedAt() { return inspectedAt; }
    public void setInspectedAt(String inspectedAt) { this.inspectedAt = inspectedAt; }
    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }
}
