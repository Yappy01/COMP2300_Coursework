package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Main {
    public static void main(String[] args) {
        // Your full Neon connection string
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");      // extract from connection string
        String password = System.getenv("DB_PASSWORD");  // extract from connection string

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to Neon successfully!");

            // Example query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    ""
            );

            if (rs.next()) {
                System.out.println("PostgreSQL version: " + rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}