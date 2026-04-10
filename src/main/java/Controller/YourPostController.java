package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;

public class YourPostController {
    @FXML private ProgressIndicator progressIndicator;
    private final CommunityPageController communityPageController = new CommunityPageController();

    public void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }

    public void onAddButtonClick() {
        communityPageController.onAddButtonClick();
    }

    @FXML
    public static void goToYourPost(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                HomePageController.class.getResource("/fxml/pages/yourPost.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

    public void goToPIPage(ActionEvent event) throws IOException {
        UserProfileController.gotoPIPage(event);
    }

    public void initialize(){
        System.out.println("yourPostController initialize");
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);
    }
}
