package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;
import javafx.concurrent.Task;
import utils.General;
import utils.Session;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PostService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
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

    public void getCommentsAsync(Post post, Consumer<ArrayList<Comment>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<Comment>> task = new Task<ArrayList<Comment>>() {
            @Override
            protected ArrayList<Comment> call() throws Exception {
                return postDatabase.getComment(post.getPostId(), 12);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void getAllPostsAsync(String type, Consumer<ArrayList<Post>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<Post>> task = new Task<ArrayList<Post>>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                return postDatabase.getCard(12, type);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void insertPostAsync(Post post, Runnable onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return postDatabase.insertPost(post);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }
}
