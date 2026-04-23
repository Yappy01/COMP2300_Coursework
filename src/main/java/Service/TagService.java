package Service;

import DBHandling.TagDatabase;
import Models.Tag;
import javafx.concurrent.Task;
import utils.General;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TagService {

    private final TagDatabase tagDatabase = new TagDatabase();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void insertTagsAsync(int postId, ArrayList<Tag> content, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed){
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                for (int i = 0; i < content.size(); i++) {
                    if (!tagDatabase.insertTags(postId, content.get(i))) {
                        return false;
                    }
                };
                return true;
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }
}
