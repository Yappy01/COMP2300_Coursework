package DBHandling;

import Models.Event;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDatabase {
    public void saveEvent(String title, String desc, Timestamp time) {
//        int userId = UserSession.getInstance().getUserId();
        String sql = "INSERT INTO events (name, description, date_time, fk_userid, fk_typeid) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, desc);
            pstmt.setTimestamp(3, time);
            pstmt.setInt(4, 15); //foreign key
            pstmt.setInt(5, 1);

            pstmt.executeUpdate();
            System.out.println("Event successfully recorded!");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }



    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT date_time, name, description FROM events WHERE fk_userid = ? AND fk_typeid = ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, 15);
            stmt.setInt(2, 1);


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("date_time");
                    LocalDateTime ldt = ts.toLocalDateTime();

                    String formattedDate = ldt.format(DateTimeFormatter.ofPattern("dd MMM yyyy")).toUpperCase();
                    String formattedTime = ldt.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();

                    Event event = new Event(
                                formattedDate,
                                formattedTime,
                                rs.getString("name"),
                                rs.getString("description")
                    );
                    eventList.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return eventList;
    }
}
