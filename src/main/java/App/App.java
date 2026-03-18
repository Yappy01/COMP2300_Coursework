package App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.UIConstant;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LRFDocument_updated.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root,UIConstant.login_WIDTH,UIConstant.login_HEIGHT);
        primaryStage.setTitle("Sexual Health Application");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}
