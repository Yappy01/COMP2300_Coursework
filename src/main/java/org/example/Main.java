package org.example;
import org.example.config.DBConnection;
import org.example.model.User;
import org.example.repository.UserRepository;

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

//            User user1 = new User("Grace", "Grace123", "Grace28@gmail.com");
//            UserRepository userRepository = new UserRepository();
//            userRepository.save_user(user1);
//            userRepository.secureLogin("Grace", "Grace123");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}