package App;

import Service.UserService;
import com.cloudinary.Cloudinary;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.Session;

public class CalendarTest extends Application {

    private final UserService userService = new UserService();
    @Override
    public void start(Stage primaryStage) throws Exception {
        Session.startSession(userService.searchByUsername("jiyou"));

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/UserProfile.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Custom JavaFX Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}