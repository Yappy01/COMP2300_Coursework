package DBHandling;

import Models.Comment;
import Models.Post;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class ComPostDatabase {

    // 1️⃣ Create (Insert Post)
    public boolean insertPost(Post post) {
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
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2️⃣ Read (By ID)
    public Post findById(int id) {
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
    public ArrayList<Post> getAll() {
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

    public ArrayList<Post> getCard(int limit, String type) {
        ArrayList<Post> list = new ArrayList<>();
        // Use '?' as a placeholder for the limit
        String orderBy ="createdAt";
        String sql;

        if (type.equals("recent")) {
            orderBy = "createdat";
        } else if (type.equals("likes")) {
            orderBy = "likecount";
        } else if (type.equals("comments")) {
            orderBy = "commentcount";
        }
        sql = "SELECT * FROM posts ORDER BY " + orderBy + " DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the value for the placeholder
            stmt.setInt(1, limit);

            System.out.println(stmt);

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

    public ArrayList<Comment> getComment(int postId, int limit) {
        ArrayList<Comment> list = new ArrayList<>();
        // Use '?' as a placeholder for the limit
        String sql = "SELECT * FROM comments where postId = ? ORDER BY createdAt DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the value for the placeholder
            stmt.setInt(1, postId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                            rs.getInt("commentId"),
                            rs.getInt("postId"),
                            rs.getInt("userId"),
                            rs.getString("content"),
                            rs.getTimestamp("createdAt"),
                            rs.getTimestamp("updatedAt")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 4️⃣ Filter by User
    public ArrayList<Post> getPostsByUser(int userId) {
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
    public ArrayList<Post> searchByContent(String keyword) {
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
    public void update(Post post) {
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
    public Boolean delete(int id) {
        String sql = "DELETE FROM posts WHERE postId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Post> searchPosts(
            Integer userId,
            String content,
            Timestamp date,
            Integer likeCount,
            Integer commentCount,
            String filePath
    ) {
        ArrayList<Post> results = new ArrayList<>();

        String sql = "SELECT * FROM posts WHERE userid = ? AND content = ? AND createdat = ? AND imagelink = ? AND commentCount = ? AND likeCount = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, content);
            pstmt.setTimestamp(3, date);
            pstmt.setString(4, filePath);
            pstmt.setInt(5, commentCount);
            pstmt.setInt(6, likeCount);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(extractPost(rs)); // assumes you already implemented this
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // 8️⃣ Like Post (Increment)
    public boolean toggleLike(int postId, int userId) {
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

    public boolean addComment(int postId, int userId, String content) {
        String insertComment = "INSERT INTO comments (postid, userid, content) VALUES (?, ?, ?)";
        // Optional: If your 'posts' table has a 'commentCount' column to track totals
        String updatePostCount = "UPDATE posts SET commentCount = commentCount + 1 WHERE postId = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement st1 = conn.prepareStatement(insertComment);
                 PreparedStatement st2 = conn.prepareStatement(updatePostCount)) {

                // 1. Insert the new comment
                st1.setInt(1, postId);
                st1.setInt(2, userId);
                st1.setString(3, content);
                st1.executeUpdate();

                // 2. Update the post's comment counter
                st2.setInt(1, postId);
                st2.executeUpdate();

                conn.commit(); // Success!
                return true;
            } catch (SQLException e) {
                conn.rollback(); // If anything fails, don't save the comment
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔧 Helper Method
    private Post extractPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("postId"),
                rs.getInt("userId"),
                rs.getString("content"),
                rs.getInt("likeCount"),
                rs.getString("imageLink"),
                rs.getTimestamp("createdAt"),
                rs.getTimestamp("updatedAt"),
                rs.getInt("commentCount"),
                rs.getString("postId")
        );
    }
}
