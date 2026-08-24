package ssam.model;

/**
 * Lop cho du lieu. DA VIET SAN, khong phai sua.
 * Ket qua duyet cua QA. Cot decision nhan Approve hoac Reject.
 */
public class ReviewDto {

    private String reviewID;
    private String anomalyID;
    private String reviewedBy;
    private String decision;
    private String comment;
    private String reviewedAt;

    // Cac truong duoi day KHONG phai cot cua bang,
    // chung lay ve nho phep noi bang trong cau lenh truy van.
    private String reviewerName;

    public ReviewDto() {
    }

    public ReviewDto(String reviewID, String anomalyID, String reviewedBy, String decision, String comment, String reviewedAt) {
        this.reviewID = reviewID;
        this.anomalyID = anomalyID;
        this.reviewedBy = reviewedBy;
        this.decision = decision;
        this.comment = comment;
        this.reviewedAt = reviewedAt;
    }

    public String getReviewID() { return reviewID; }
    public void setReviewID(String reviewID) { this.reviewID = reviewID; }
    public String getAnomalyID() { return anomalyID; }
    public void setAnomalyID(String anomalyID) { this.anomalyID = anomalyID; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
}
