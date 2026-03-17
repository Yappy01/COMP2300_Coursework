package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class ComPageOverlayController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private VBox cardBox;
    @FXML
    private Button likeBtn;
    @FXML
    private Button commentBtn;
    @FXML
    private VBox cardBoxContent;
    @FXML
    private ImageView contentImage;
    @FXML
    private CommunityPageController parentController;
    @FXML
    private StackPane mainPostPage;

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    public void setOverlayData(String name, String content, String date) {
        this.nameLabel.setText(name);
        this.contentLabel.setText(content);
        this.dateLabel.setText(date);
    }

    public void clickExitButton() {
        parentController.setOverlayVisibility(false);
    }
}
