package Models;

public class Report {
    private int postId;
    private int userId;
    private String status ;
    private String reason ;

    public Report(int postId, int userId, String status, String reason) {
        this.postId = postId;
        this.userId = userId;
        this.status = status;
        this.reason = reason;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
