package App;
import utils.DBConnection;

import java.sql.*;

public class Main {
    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Connected to Neon successfully!");

            // Example query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM users"
            );

            while (rs.next()) {
                System.out.println("Tables: " + rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}