package DBHandling;

import Models.Comment;
import Models.Post;
import Models.Tag;
import Models.User;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class ComPostDatabase {

    public ArrayList<Post> getPostsByTag(String tagName) {
        ArrayList<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, STRING_AGG(t_all.name, ',') AS tag_list " +
                "FROM posts p " +
                "JOIN post_tags pt_filter ON p.postId = pt_filter.postid " +
                "JOIN tags t_filter ON pt_filter.tagid = t_filter.tagId " +
                "LEFT JOIN post_tags pt_all ON p.postId = pt_all.postid " +
                "LEFT JOIN tags t_all ON pt_all.tagid = t_all.tagId " +
                "WHERE t_filter.name = ? " +
                "GROUP BY p.postId " +
                "ORDER BY p.createdAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tagName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Integer countPostsToday(User user) {
        String sql = "SELECT COUNT(*) FROM posts WHERE userid = ? AND createdat >= CURRENT_DATE;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, user.getUserId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 1️⃣ Create (Insert Post)
    public boolean insertPost(Post post) {
        String sql = "INSERT INTO posts (userId, content, likeCount, imageLink, createdAt, updatedAt, publicid) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, 0);
            stmt.setString(4, post.getImageLink());
            stmt.setTimestamp(5, post.getCreatedAt());
            stmt.setTimestamp(6, post.getUpdatedAt());
            stmt.setString(7, post.getPublicId());

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

    // 3️⃣ Get All Posts
    public ArrayList<Post> getAll() {
        ArrayList<Post> list = new ArrayList<>();

        // Updated SQL using STRING_AGG to collect tags into one column
        String sql = "SELECT p.*, STRING_AGG(t.name, ',') AS tag_list " +
                "FROM posts p " +
                "LEFT JOIN post_tags pt ON p.postId = pt.postid " +
                "LEFT JOIN tags t ON pt.tagid = t.tagId " +
                "GROUP BY p.postId " +
                "ORDER BY p.createdAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Your existing extractPost will now handle the "tag_list" column
                list.add(extractPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Post> getCard(int limit, String type, Boolean showDeleted) {
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
        String baseQuery = "SELECT p.*, STRING_AGG(t.name, ',') AS tag_list " +
                "FROM posts p " +
                "LEFT JOIN post_tags pt ON p.postId = pt.postid " +
                "LEFT JOIN tags t ON pt.tagid = t.tagId ";

        if (showDeleted) {
            // Selects everything
            sql = baseQuery + "GROUP BY p.postId ORDER BY p." + orderBy + " DESC LIMIT ?";
        } else {
            // Selects only active posts
            sql = baseQuery + "WHERE p.reasonDeleted IS NULL " +
                    "GROUP BY p.postId ORDER BY p." + orderBy + " DESC LIMIT ?";
        }
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
    public ArrayList<Post> getPostsByUser(Integer userId, Integer limit) {
        ArrayList<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, STRING_AGG(t.name, ',') AS tag_list " +
                "FROM posts p " +
                "LEFT JOIN post_tags pt ON p.postId = pt.postid " +
                "LEFT JOIN tags t ON pt.tagid = t.tagId " +
                "WHERE p.userId = ? " +
                "GROUP BY p.postId " +
                "ORDER BY p.createdAt DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Post> getPostsByRole(String role, Integer limit) {
        ArrayList<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, STRING_AGG(t.name, ',') AS tag_list " +
                "FROM posts p " +
                "JOIN users u ON p.userId = u.\"userId\" " +
                "LEFT JOIN post_tags pt ON p.postId = pt.postid " +
                "LEFT JOIN tags t ON pt.tagid = t.tagId " +
                "WHERE u.role = ? " +
                "GROUP BY p.postId " +
                "ORDER BY p.createdAt DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setInt(2, limit);
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
    public Boolean update(Post post) {
        String sql = "UPDATE posts SET content=?, imageLink=?, updatedAt=?, publicId = ? WHERE postId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getContent());
            stmt.setString(2, post.getImageLink());
            stmt.setTimestamp(3, post.getUpdatedAt());
            stmt.setString(4, post.getPublicId());
            stmt.setInt(5, post.getPostId());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Temporary Delete Posts
    public Boolean tempDelete(int id, String text) {
        String sql = "UPDATE posts SET reasonDeleted = ? WHERE postId=?";;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, text);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public Post searchPostById(int postId) {
        String sql = "SELECT p.*, STRING_AGG(t.name, ',') AS tag_list " +
                "FROM posts p " +
                "LEFT JOIN post_tags pt ON p.postId = pt.postid " +
                "LEFT JOIN tags t ON pt.tagid = t.tagId " +
                "WHERE p.postId = ? " +
                "GROUP BY p.postId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPost(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

        String sql = "SELECT p.*, STRING_AGG(t.name, ',') AS tag_list " +
                "FROM posts p " +
                "LEFT JOIN post_tags pt ON p.postId = pt.postid " +
                "LEFT JOIN tags t ON pt.tagid = t.tagId " +
                "WHERE p.userid = ? " +
                "  AND p.content = ? " +
                "  AND p.createdat = ? " +
                "  AND p.imagelink = ? " +
                "  AND p.commentCount = ? " +
                "  AND p.likeCount = ? " +
                "GROUP BY p.postId";
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
        Post post = new Post(
                rs.getInt("postId"),
                rs.getInt("userId"),
                rs.getString("content"),
                rs.getInt("likeCount"),
                rs.getString("imageLink"),
                rs.getTimestamp("createdAt"),
                rs.getTimestamp("updatedAt"),
                rs.getInt("commentCount"),
                rs.getString("publicId"),
                rs.getString("reasonDeleted")
        );

        String rawTags = rs.getString("tag_list");
        ArrayList<Tag> tagObjects = new ArrayList<>();

        if (rawTags != null && !rawTags.isBlank()) {
            // 1. Split the comma-separated string into names
            String[] tagNames = rawTags.split(",");

            // 2. Convert each name into a Tag object and add to the list
            for (String name : tagNames) {
                tagObjects.add(new Tag(name.trim()));
            }
        }

        // 3. Set the list of Tag objects to the post
        post.setTagsList(tagObjects);

        return post;
    }
}
