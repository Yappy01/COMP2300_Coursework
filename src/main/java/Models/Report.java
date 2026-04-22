package Models;

public class Report {
    private int postId;
    private int userId;
    private String status ;
    private String reason ;
    private String how_its_resolved;

    public String getHow_its_resolved() {
        return how_its_resolved;
    }

    public void setHow_its_resolved(String how_its_resolved) {
        this.how_its_resolved = how_its_resolved;
    }

    public Report(int postId, int userId, String status, String reason, String how_its_resolved) {
        this.postId = postId;
        this.userId = userId;
        this.status = status;
        this.reason = reason;
        this.how_its_resolved = how_its_resolved;
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
