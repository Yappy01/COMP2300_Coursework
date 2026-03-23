package Controller;

import DBHandling.ComPostDatabase;
import Models.Post;
import Models.User;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import utils.Session;

import java.io.File;
import java.io.IOException;

public class OverlayBController {
    @FXML
    private CommunityPageController parentController;

    @FXML
    private TextArea postText;

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void clickExitButton() {
        parentController.setOverlayBVisibility(false);
    }

    @FXML
    public void insertPost() {
        User user = Session.getInstance().getUser();
        Session.startSession(user);

        Post post = new Post(user.getUserId(), postText.getText(), "");
        ComPostDatabase.insertPost(post);
    }
//
//    @FXML
//    public void uploadImage() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select an Image File");
//
//        // Optional: Filter for specific extensions
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
//        );
//
//        // Show the dialog (pass your current Stage/Window)
//        File selectedFile = fileChooser.showOpenDialog();
//
//        if (selectedFile != null) {
//            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
//            // You can now read the file or set its path to your TextArea
//    }
//
//    @FXML
//    public void likeClicked(ActionEvent event) throws IOException {
//
//    }
}
