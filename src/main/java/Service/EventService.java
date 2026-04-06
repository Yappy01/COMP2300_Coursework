package Service;

import DBHandling.EventDatabase;
import Models.UserEvent;
import javafx.concurrent.Task;
import utils.Session;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    EventDatabase database = new EventDatabase();

    public void deleteEventAsync(String title, Integer userId,
                                 Runnable onSucceeded, Consumer<Throwable> onFailed) {

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // All database logic stays here
                return database.deleteEvent(title, userId);
            }
        };

        task.setOnSucceeded(e -> onSucceeded.run());
        task.setOnFailed(e -> onFailed.accept(task.getException()));

        executor.submit(task);
    }


    public void saveEventAsync(String title, String desc, Timestamp time, Integer userid, Integer typeid,
                               Runnable onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return database.saveEvent(title, desc, time, userid, typeid);
            }
        };

        task.setOnSucceeded(e -> onSucceeded.run());
        task.setOnFailed(e -> onFailed.accept(task.getException()));
        executor.submit(task);
    }

    public void getAllEventsAsync(Consumer<List<UserEvent>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<List<UserEvent>> task = new Task<List<UserEvent>>() {
            @Override
            protected List<UserEvent> call() throws Exception {
                return database.getAllEvents();
            }
        };

        task.setOnSucceeded(e -> onSucceeded.accept(task.getValue()));
        task.setOnFailed(e -> onFailed.accept(task.getException()));
        executor.submit(task);
    }

    public void getFilteredEventsAsync(int userId, int typeId, String timeMode, Consumer<List<UserEvent>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<List<UserEvent>> task = new Task<>() {
            @Override
            protected List<UserEvent> call() throws Exception {
                return database.getFilteredEvents(Session.getInstance().getUserID(), typeId, timeMode);
            }
        };

        task.setOnSucceeded(e -> onSucceeded.accept(task.getValue()));
        task.setOnFailed(e -> onFailed.accept(task.getException()));
        executor.submit(task);
    }
}
