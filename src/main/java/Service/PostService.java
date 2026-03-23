package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;

import java.util.ArrayList;

public class PostService {
    public void likePost(Post post) {
        ComPostDatabase.toggleLike(post.getPostId(), post.getUserId());
    }

    public void commentPost(Post post, String content) {
        ComPostDatabase.addComment(post.getPostId(), post.getUserId(), content);
    }

    public ArrayList<Comment> getComments(Post post) {
        try {
            return ComPostDatabase.getRecentComment(post.getPostId(), 12);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Comment>();
        }
    }
}
