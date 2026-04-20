package App;

import Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;

import static javafx.application.Application.launch;

public class AdminPage extends Application {
    private final UserService userService = new UserService();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Session.startSession(userService.searchByUsername("Admin"));

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/adminMain.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Custom JavaFX Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
