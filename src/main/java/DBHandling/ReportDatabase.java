package DBHandling;

import Models.Post;
import Models.Report;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class ReportDatabase {
    public ArrayList<Report> getAllReports() {
        ArrayList<Report> reports = new ArrayList<>();
        String query = "SELECT * FROM reports";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reports.add(extractReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public void resolveReport(int postId, int userId, String resolutionDetails) {
        // We use a prepared statement to prevent SQL injection
        String query = "UPDATE reports SET status = 'Resolved', how_its_resolved = ? " +
                "WHERE postId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareCall(query)) {

            pstmt.setString(1, resolutionDetails);
            pstmt.setInt(2, postId);
            pstmt.setInt(3, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Report marked as resolved successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean toggleReport(int postId, int userId, String reason) {
        String submitReport = "INSERT INTO post_report (postid, userid, status, reason) VALUES (?, ?, 'pending', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(submitReport)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            stmt.setString(3, reason);

            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Report extractReport(ResultSet rs) throws SQLException {
        return new Report(
                rs.getInt("postId"),
                rs.getInt("userId"),
                rs.getString("status"),
                rs.getString("reason"),
                rs.getString("how_its_resolved")
        );
    }
}
