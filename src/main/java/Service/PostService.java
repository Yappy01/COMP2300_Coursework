package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;
import utils.Session;

import java.util.ArrayList;

public class PostService {
    public static void likePost(Post post) {
        ComPostDatabase.toggleLike(post.getPostId(), Session.getInstance().getUser().getUserId());
    }

    public static void commentPost(Post post, String content) {
        ComPostDatabase.addComment(post.getPostId(), post.getUserId(), content);
    }

    public static ArrayList<Comment> getComments(Post post) {
        try {
            return ComPostDatabase.getComment(post.getPostId(), 12);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Comment>();
        }
    }

    public static ArrayList<Post> getAllPosts(String type) {
        return ComPostDatabase.getCard(12, type);
    }
}
