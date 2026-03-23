package DBHandling;

import Models.Post;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class ComPostDatabase {

    // 1️⃣ Create (Insert Post)
    public static void insertPost(Post post) {
        String sql = "INSERT INTO posts (userId, content, likeCount, imageLink, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, 0);
            stmt.setString(4, post.getImageLink());
            stmt.setTimestamp(5, post.getCreatedAt());
            stmt.setTimestamp(6, post.getUpdatedAt());

            stmt.executeUpdate();

            // Get generated ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                post.setPostId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2️⃣ Read (By ID)
    public static Post findById(int id) {
        String sql = "SELECT * FROM posts WHERE postId = ?";
        Post post = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                post = extractPost(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return post;
    }

    // 3️⃣ Get All Posts
    public static ArrayList<Post> getAll() {
        ArrayList<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY createdAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<Post> getRecent(int limit) {
        ArrayList<Post> list = new ArrayList<>();
        // Use '?' as a placeholder for the limit
        String sql = "SELECT * FROM posts ORDER BY createdAt DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the value for the placeholder
            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPost(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 4️⃣ Filter by User
    public static ArrayList<Post> getPostsByUser(int userId) {
        ArrayList<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE userId = ? ORDER BY createdAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 5️⃣ Search by Content (Keyword)
    public static ArrayList<Post> searchByContent(String keyword) {
        ArrayList<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE content LIKE ? ORDER BY createdAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 6️⃣ Update Post
    public static void update(Post post) {
        String sql = "UPDATE posts SET content=?, imageLink=?, updatedAt=? WHERE postId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getContent());
            stmt.setString(2, post.getImageLink());
            stmt.setTimestamp(3, post.getUpdatedAt());
            stmt.setInt(4, post.getPostId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 7️⃣ Delete Post
    public static void delete(int id) {
        String sql = "DELETE FROM posts WHERE postId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 8️⃣ Like Post (Increment)
    public static void incrementLike(int postId) {
        String sql = "UPDATE posts SET likeCount = likeCount + 1 WHERE postId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔧 Helper Method
    private static Post extractPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("postId"),
                rs.getInt("userId"),
                rs.getString("content"),
                rs.getInt("likeCount"),
                rs.getString("imageLink"),
                rs.getTimestamp("createdAt"),
                rs.getTimestamp("updatedAt")
        );
    }
}
