package DBHandling;

import Models.Report;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class ReportDatabase {
    public ArrayList<Report> getAllReports() {
        ArrayList<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, u.name FROM post_report r JOIN users u ON r.userID = u.\"userId\"";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(extractReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public Boolean resolveReport(int reportId, String resolutionDetails) {
        // We use a prepared statement to prevent SQL injection
        String query = "UPDATE post_report SET status = 'Resolved', how_its_resolved = ? " +
                "WHERE report_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareCall(query)) {

            pstmt.setString(1, resolutionDetails);
            pstmt.setInt(2, reportId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleReport(int postId, int userId, String reason, Timestamp deleted_at) {
        String submitReport = "INSERT INTO post_report (postid, userid, status, reason, deleted_at) VALUES (?, ?, 'pending', ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(submitReport)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            stmt.setString(3, reason);
            stmt.setTimestamp(4, deleted_at);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean softDelete(int reportId) {
        String sql = "UPDATE post_report SET deleted_at = ? WHERE report_id=?";;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, reportId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Report extractReport(ResultSet rs) throws SQLException {
        return new Report(
                rs.getInt("report_id"),
                rs.getInt("postId"),
                rs.getString("name"),
                rs.getString("status"),
                rs.getString("reason"),
                rs.getString("how_its_resolved"),
                rs.getTimestamp("deleted_at")
        );
    }


}
