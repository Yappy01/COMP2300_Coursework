package DBHandling;

import Models.Quiz;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDatabase {

    public List<Quiz> getQuizzesByTypeId(int typeId) {
        List<Quiz> quizList = new ArrayList<>();
        // Query filters by typeId (1 for STI, 2 for Birth Control, etc.)
        String query = "SELECT question, answer1, answer2, answer3, answer4, \"correctAnswer\" FROM quiz WHERE fk_typeid = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, typeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizList.add(new Quiz(
                            rs.getString("question"),
                            rs.getString("answer1"),
                            rs.getString("answer2"),
                            rs.getString("answer3"),
                            rs.getString("answer4"),
                            rs.getString("correctAnswer")
                    ));
//                    System.out.println(rs.getString("correctAnswer"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Quiz Database error: " + e.getMessage());
            e.printStackTrace();
        }
//        System.out.println("Quiz Database returned from quiz database: " + quizList);
        return quizList;
    }
}