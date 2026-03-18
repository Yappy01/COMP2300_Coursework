package App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.util.Objects;

public class InformationPage extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/fxml/pages/information.fxml"))
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("My Window");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}