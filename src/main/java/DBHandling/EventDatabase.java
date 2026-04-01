package DBHandling;

import Models.UserEvent;
import utils.DBConnection;
import utils.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDatabase {
    public void saveEvent(String title, String desc, Timestamp time,Integer userid, Integer typeid) {
        String sql = "INSERT INTO events (name, description, date_time, fk_userid, fk_typeid) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, desc);
            pstmt.setTimestamp(3, time);
            pstmt.setInt(4, userid); //foreign key
            pstmt.setInt(5, typeid); //foreign key

            pstmt.executeUpdate();
            System.out.println("Event successfully recorded!");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }



    public List<UserEvent> getAllEvents() {
        List<UserEvent> userEventList = new ArrayList<>();
        String query = "SELECT date_time, name, description FROM events WHERE fk_userid = ? AND fk_typeid = ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.getInstance().getUserID());
            stmt.setInt(2, 1);


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("date_time");
                    LocalDateTime ldt = ts.toLocalDateTime();

                    String formattedDate = ldt.format(DateTimeFormatter.ofPattern("dd MMM yyyy")).toUpperCase();
                    String formattedTime = ldt.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();

                    UserEvent userEvent = new UserEvent(
                                formattedDate,
                                formattedTime,
                                rs.getString("name"),
                                rs.getString("description")
                    );
                    userEventList.add(userEvent);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return userEventList;
    }

    public boolean deleteEvent(String eventName, int userId) {
        String query = "DELETE FROM events WHERE name = ? AND fk_userid = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, eventName);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Delete error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<UserEvent> getFilteredEvents(int userId, int typeId, String timeMode) {
        List<UserEvent> eventList = new ArrayList<>();
        String query = "SELECT date_time, name, description FROM events WHERE fk_userid = ? AND fk_typeid = ?";

        // Add date filtering based on timeMode
        if (timeMode.equals("FUTURE")) {
            query += " AND date_time >= CURRENT_TIMESTAMP ORDER BY date_time ASC";
        } else if (timeMode.equals("PAST")) {
            query += " AND date_time < CURRENT_TIMESTAMP ORDER BY date_time DESC";
        } else {
            query += " ORDER BY date_time ASC"; // Default for treatments
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, typeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("date_time");
                    LocalDateTime ldt = ts.toLocalDateTime();

                    String formattedDate = ldt.format(DateTimeFormatter.ofPattern("dd MMM yyyy")).toUpperCase();
                    String formattedTime = ldt.format(DateTimeFormatter.ofPattern("hh:mm ")).toUpperCase();

                    eventList.add(new UserEvent(formattedDate, formattedTime, rs.getString("name"), rs.getString("description")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }
}
