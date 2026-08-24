package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Cot status nhan Running, Maintenance hoac Retired.
 */
public class EquipmentDto {

    private String equipmentID;
    private String equipmentName;
    private String area;
    private String model;
    private String status;

    public EquipmentDto() {
    }

    public EquipmentDto(String equipmentID, String equipmentName, String area, String model, String status) {
        this.equipmentID = equipmentID;
        this.equipmentName = equipmentName;
        this.area = area;
        this.model = model;
        this.status = status;
    }

    public String getEquipmentID() { return equipmentID; }
    public void setEquipmentID(String equipmentID) { this.equipmentID = equipmentID; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
