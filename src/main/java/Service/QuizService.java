package Service;

import DBHandling.QuizDatabase;
import Models.Quiz;
import javafx.concurrent.Task;
import utils.General;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class QuizService {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final QuizDatabase database = new QuizDatabase();

    public void getQuizzesAsync(int typeId, Consumer<List<Quiz>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<List<Quiz>> task = new Task<>() {
            @Override
            protected List<Quiz> call() throws Exception {
                return database.getQuizzesByTypeId(typeId);
            }
        };

        // Uses your General utility to manage the task life cycle
        General.setTask(task, onSucceeded, onFailed, executor);
    }
}