package Service;

import DBHandling.ComPostDatabase;
import Models.Post;

public class PostService {
    public Boolean likePost(Post post) {
        return ComPostDatabase.toggleLike(post.getPostId(), post.getUserId());
    }
}
