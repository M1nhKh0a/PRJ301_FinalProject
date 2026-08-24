package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Lich truc ca. Cot shiftType nhan Day hoac Night.
 */
public class ShiftDto {

    private String shiftID;
    private String userID;
    private String shiftDate;
    private String shiftType;
    private String area;

    // Cac truong duoi day KHONG phai cot cua bang,
    // chung lay ve nho phep noi bang trong cau lenh truy van.
    private String fullName;

    public ShiftDto() {
    }

    public ShiftDto(String shiftID, String userID, String shiftDate, String shiftType, String area) {
        this.shiftID = shiftID;
        this.userID = userID;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.area = area;
    }

    public String getShiftID() { return shiftID; }
    public void setShiftID(String shiftID) { this.shiftID = shiftID; }
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getShiftDate() { return shiftDate; }
    public void setShiftDate(String shiftDate) { this.shiftDate = shiftDate; }
    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
