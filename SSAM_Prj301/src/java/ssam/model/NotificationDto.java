package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Thong bao gui toi mot nguoi cu the.
 */
public class NotificationDto {

    private String notificationID;
    private String anomalyID;
    private String userID;
    private String message;
    private String createdAt;
    private boolean isRead;

    // Cac truong duoi day KHONG phai cot cua bang,
    // chung lay ve nho phep noi bang trong cau lenh truy van.
    private String severity;
    private String anomalyStatus;

    public NotificationDto() {
    }

    public NotificationDto(String notificationID, String anomalyID, String userID, String message, String createdAt, boolean isRead) {
        this.notificationID = notificationID;
        this.anomalyID = anomalyID;
        this.userID = userID;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public String getNotificationID() { return notificationID; }
    public void setNotificationID(String notificationID) { this.notificationID = notificationID; }
    public String getAnomalyID() { return anomalyID; }
    public void setAnomalyID(String anomalyID) { this.anomalyID = anomalyID; }
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public boolean isIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getAnomalyStatus() { return anomalyStatus; }
    public void setAnomalyStatus(String anomalyStatus) { this.anomalyStatus = anomalyStatus; }
}
