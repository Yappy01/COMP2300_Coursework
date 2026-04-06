package utils;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class General {

    public static String formatLikes(int count) {
        if (count >= 1_000_000)
            return String.format("%.1fM", count / 1_000_000.0);
        if (count >= 1000)
            return String.format("%.1fk", count / 1000.0);
        return String.valueOf(count);
    }

    public static <T> void setTask(Task<T> task, Consumer<T> onSucceeded, Consumer<Throwable> onFailed, ExecutorService executor) {
        task.setOnSucceeded(e -> {onSucceeded.accept(task.getValue());});
        task.setOnFailed(e -> {onFailed.accept(task.getException());});
        executor.execute(task);
    }

    public static <T> void setTask(Task<T> task, Runnable onSucceeded, Consumer<Throwable> onFailed, ExecutorService executor) {
        task.setOnSucceeded(e -> {onSucceeded.run();});
        task.setOnFailed(e -> {onFailed.accept(task.getException());});
        executor.execute(task);
    }

    public static void getInfoAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void getErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
