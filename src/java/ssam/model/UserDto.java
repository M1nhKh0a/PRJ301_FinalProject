package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Nam vai tro: AD quan tri, OP van hanh, EN ky su, QA, QC.
 */
public class UserDto {

    private String userID;
    private String fullName;
    private String email;
    private String password;
    private String roleID;
    private String area;
    private boolean status;

    public UserDto() {
    }

    public UserDto(String userID, String fullName, String email, String password, String roleID, String area, boolean status) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.roleID = roleID;
        this.area = area;
        this.status = status;
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    /** Tien cho trang jsp kiem tra vai tro. */
    public boolean isAdmin()    { return "AD".equals(roleID); }
    public boolean isOperator() { return "OP".equals(roleID); }
    public boolean isEngineer() { return "EN".equals(roleID); }
    public boolean isQa()       { return "QA".equals(roleID); }
    public boolean isQc()       { return "QC".equals(roleID); }
}
