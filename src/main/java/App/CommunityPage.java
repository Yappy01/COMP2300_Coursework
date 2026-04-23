package App;

import Models.User;
import Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Session;

import java.util.Objects;

public class CommunityPage extends Application {

    private final UserService userService = new UserService();
    @Override
    public void start(Stage stage) throws Exception {
        Session.startSession(userService.searchByUsername("Admin"));
        System.out.println("Session Started");
        System.out.println(Session.getInstance().getUserName());

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/fxml/pages/community.fxml"))
        );
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Community Page");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
