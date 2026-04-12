package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import net.bytebuddy.implementation.bytecode.Throw;
import utils.DBConnection;
import utils.General;
import utils.Session;

import javax.swing.text.TableView;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PostService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ComPostDatabase postDatabase = new ComPostDatabase();
    private final Cloudinary cloudinary = DBConnection.cloudinary();
    private final Map<Integer, Long> lastPostTime = new HashMap<>();

    public boolean canPost(Integer userId) {
        long now = System.currentTimeMillis();

        if (lastPostTime.containsKey(userId)) {
            long last = lastPostTime.get(userId);

            if (now - last < 3000) { // 3 sec cooldown
                return false;
            }
        }

        lastPostTime.put(userId, now);
        return true;
    }


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

    public void getAllPostsAsync(String type, Integer limit, Consumer<ArrayList<Post>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<Post>> task = new Task<ArrayList<Post>>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                return postDatabase.getCard(limit, type);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void getPostByUserAsync(Integer userId, Integer limit, Consumer<ArrayList<Post>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<Post>> task = new Task<ArrayList<Post>>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                return postDatabase.getPostsByUser(userId, limit);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void insertPostAsync(Post post, File selectedFile, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (!canPost(post.getUserId())) {
                    return false;
                }

                if (selectedFile != null) {
                    Map uploadResult = cloudinary.uploader().upload(selectedFile, ObjectUtils.emptyMap());

                    String imageUrl = (String) uploadResult.get("secure_url");
                    String publicId = (String) uploadResult.get("public_id");
                    post.setImageLink(imageUrl); // 🔥 store URL, not file path
                    post.setPublicId(publicId);
                    System.out.println(publicId + " there  HELLLLOOO\n");
                }
                return postDatabase.insertPost(post);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void deletePostAsync(Post post, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (!post.getPublicId().isEmpty()) {
                    Map result = cloudinary.uploader().destroy(post.getPublicId(), ObjectUtils.emptyMap());
                    String status = (String) result.get("result");

                    // 🔥 Check if deletion succeeded
                    if (!"ok".equals(status) && !"not found".equals(status)) {
                        throw new RuntimeException("Cloudinary deletion failed: " + status);
                    }
                    System.out.println("PUBLiC ID =" + post.getPublicId());
                    System.out.println(result);
                }
                return postDatabase.delete(post.getPostId());
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void editPostAsync(Post post, File selectedFile, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (selectedFile != null) {
                    if (!post.getPublicId().isEmpty()) {
                        Map result = cloudinary.uploader().destroy(post.getPublicId(), ObjectUtils.emptyMap());
                        System.out.println(result);
                    }

                    Map uploadResult = cloudinary.uploader().upload(selectedFile, ObjectUtils.emptyMap());

                    String imageUrl = (String) uploadResult.get("secure_url");
                    String publicId = (String) uploadResult.get("public_id");
                    post.setImageLink(imageUrl); // 🔥 store URL, not file path
                    post.setPublicId(publicId);
                    post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                }

                return postDatabase.update(post);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void searchPostAsync(
            Integer userId,
            String content,
            Timestamp date,
            Integer likeCount,
            Integer commentCount,
            String filePath,
            Consumer<ArrayList<Post>> onSucceeded,
            Consumer<Throwable> onFailed
    ) {
        Task<ArrayList<Post>> task = new Task<ArrayList<Post>>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                return postDatabase.searchPosts(
                        userId,
                        content,
                        date,
                        likeCount,
                        commentCount,
                        filePath
                );
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }
}
