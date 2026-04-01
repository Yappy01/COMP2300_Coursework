package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;
import javafx.concurrent.Task;
import utils.Session;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PostService {
    private final ComPostDatabase postDatabase = new ComPostDatabase();
    public void likePost(Post post) {
        postDatabase.toggleLike(post.getPostId(), Session.getInstance().getUser().getUserId());
    }

    public void commentPost(Post post, String content) {
        if (content == null || content.equals("")) {
            return ;
        }
        postDatabase.addComment(post.getPostId(), post.getUserId(), content);
    }

    public ArrayList<Comment> getComments(Post post) {
        try {
            return postDatabase.getComment(post.getPostId(), 12);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Comment>();
        }
    }

    public void getAllPostsAsync(String type, Consumer<ArrayList<Post>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<Post>> task = new Task<ArrayList<Post>>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                return postDatabase.getCard(12, type);
            }
        };

        task.setOnSucceeded(e -> onSucceeded.accept(task.getValue()));
        task.setOnFailed(e -> onFailed.accept(task.getException()));
    }

    public void insertPostAsync(Post post, Runnable onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return postDatabase.insertPost(post);
            }
        };

        task.setOnSucceeded(e -> onSucceeded.run());
        task.setOnFailed(e -> onFailed.accept(task.getException()));
    }
}
