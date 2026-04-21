package utils;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
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

    public static boolean getConfirmation(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTextInput(String title, String header) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // showAndWait() returns an Optional<String>
        Optional<String> result = dialog.showAndWait();

        // If the user clicked "OK", return the text; otherwise, return null or empty
        return result.orElse(null);
    }
}