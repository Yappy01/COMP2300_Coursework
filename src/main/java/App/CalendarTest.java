package App;

import Models.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CalendarTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        UserSession.setInstance(15, "Grace");

        Parent root = FXMLLoader.load(getClass().getResource("/App/UserProfile.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Custom JavaFX Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}