package Controller;

import Models.Post;
import Service.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.smartcardio.Card;
import java.io.IOException;


public class ComPageOverlayController {

    @FXML
    public Label overlayUsernameLabel;
    @FXML
    public Label overlayContentLabel;
    @FXML
    private CommunityPageController parentController;
    private PostService postService = new PostService();
    private Post post;

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setOverlayData(String name, String content) {
        overlayUsernameLabel.setText(name);
        overlayContentLabel.setText(content);
    }

    public void clickExitButton() {
        parentController.setOverlayVisibility(false);
    }

    public void likeClicked() {
        postService.likePost(post);
    }

    @FXML
    private void initialize() {
    }
}
