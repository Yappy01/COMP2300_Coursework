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

    public static ArrayList<Post> getRecentLiked(int userId, int limit) {
        ArrayList<Post> list = new ArrayList<>();

        // We JOIN posts and post_likes where the IDs match,
        // then filter by the specific User ID.
        String sql = "SELECT p.* FROM posts p " +
                "JOIN post_likes pl ON p.id = pl.postid " +
                "WHERE pl.userid = ? " +
                "ORDER BY p.createdAt DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Set the User ID to filter the likes
            stmt.setInt(1, userId);
            // 2. Set the Limit
            stmt.setInt(2, limit);

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
    public static boolean toggleLike(int postId, int userId) {
        // We use a transaction so both SQL commands succeed or both fail
        String insertLike = "INSERT INTO post_likes (postId, userId) VALUES (?, ?)";
        String updateCount = "UPDATE posts SET likeCount = likeCount + 1 WHERE postId = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement st1 = conn.prepareStatement(insertLike);
                 PreparedStatement st2 = conn.prepareStatement(updateCount)) {

                // 1. Record the unique like
                st1.setInt(1, postId);
                st1.setInt(2, userId);
                st1.executeUpdate();

                // 2. Update the total count
                st2.setInt(1, postId);
                st2.executeUpdate();

                conn.commit(); // Save changes
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                conn.rollback(); // Undo everything if user already liked it (Primary Key violation)
                System.out.println("User already liked this post or error occurred.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
