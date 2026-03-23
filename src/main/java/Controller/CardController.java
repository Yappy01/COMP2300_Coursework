package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class CardController {
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

    private ComPageOverlayController comPageOverlayController;

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    // Use this to set the data and contain of each card.
    public void setData(String name, String content, String date) {
        this.nameLabel.setText(name);
        this.contentLabel.setText(content);
        this.dateLabel.setText(date);
    }

    // View the entire content of the card / Bringing a small box up.
    public void cardClicked() {
        parentController.setOverlayVisibility(true);
        comPageOverlayController.setOverlayData(this.nameLabel.getText(), this.contentLabel.getText());
    }

    // This button likes the post directly
    public  void likeClicked(ActionEvent event) {

    }

    // This button allows user to directly comment without looking at other people's comment.
    public void commentClicked(ActionEvent event) {

    }

    public void setComPageOverlayController(ComPageOverlayController comPageOverlayController) {
        this.comPageOverlayController = comPageOverlayController;
    }

    @FXML
    private void initialize() {
        // This detects if the data contains an image to showcase allowing it to change between text and image.
        if (contentImage.getImage() == null) {
            contentImage.setVisible(false);
            contentImage.setManaged(false);
        } else  {
            contentImage.setVisible(true);
            contentImage.setManaged(true);
        }
    }
}
