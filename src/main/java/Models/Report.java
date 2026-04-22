package Models;

import java.sql.Timestamp;

public class Report {
    private int reportId;
    private int postId;
    private String username;
    private String status ;
    private String reason ;
    private String how_its_resolved;
    private Timestamp deleted_at;

    public Report(int reportId, int postId, String username, String status, String reason, String how_its_resolved, Timestamp deleted_at) {
        this.reportId = reportId;
        this.postId = postId;
        this.username = username;
        this.status = status;
        this.reason = reason;
        this.how_its_resolved = how_its_resolved;
        this.deleted_at = deleted_at;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getHow_its_resolved() {
        return how_its_resolved;
    }

    public void setHow_its_resolved(String how_its_resolved) {
        this.how_its_resolved = how_its_resolved;
    }

    public Timestamp getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Timestamp deleted_at) {
        this.deleted_at = deleted_at;
    }

    @Override
    public String toString() {
        return String.valueOf(reportId);
    }
}
