package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HomePage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/fxml/pages/homePage.fxml"))
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("The Sexual Health Education App");
        stage.show();
    }
}
