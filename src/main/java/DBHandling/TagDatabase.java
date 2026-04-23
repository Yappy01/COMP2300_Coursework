package DBHandling;

import Models.Tag;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagDatabase {

    public boolean insertTags(int postId, Tag content) {
        // 1. Insert tag if it doesn't exist, and return the ID regardless
        String upsertTagSQL =
                "INSERT INTO tags (name) VALUES (?) " +
                        "ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name " +
                        "RETURNING tagId";

        // 2. Link the post to the tag
        String linkPostSQL =
                "INSERT INTO post_tags (postid, tagid) VALUES (?, ?) " +
                        "ON CONFLICT DO NOTHING";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psTag = conn.prepareStatement(upsertTagSQL);
                 PreparedStatement psLink = conn.prepareStatement(linkPostSQL)) {

                // Step 1: Handle the Tag
                psTag.setString(1, content.getName().trim());
                ResultSet rs = psTag.executeQuery();

                int actualTagId = -1;
                if (rs.next()) {
                    actualTagId = rs.getInt("tagId");
                }

                // Step 2: Handle the Link
                if (actualTagId != -1) {
                    psLink.setInt(1, postId);
                    psLink.setInt(2, actualTagId);
                    psLink.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
