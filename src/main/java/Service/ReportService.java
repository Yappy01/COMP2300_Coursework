package Service;

import DBHandling.ComPostDatabase;
import DBHandling.UserRepository;
import Models.Post;
import javafx.concurrent.Task;
import utils.General;
import utils.Session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ReportService {
    private final ComPostDatabase postDatabase = new ComPostDatabase();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void reportPost(Post post, String reason, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task() {
            @Override
            protected Object call() throws Exception {
                return postDatabase.toggleReport(post.getPostId(), Session.getInstance().getUser().getUserId(), reason);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }
}
