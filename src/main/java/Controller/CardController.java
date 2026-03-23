package Controller;

import Models.Post;
import Service.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


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

    private PostService postService = new PostService();
    private Post post;

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    public void setPost(Post post) {
        this.post = post;
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
        comPageOverlayController.setPost(post);
        comPageOverlayController.setCommentSection();
    }

    // This button likes the post directly
    public void likeClicked() {
        postService.likePost(post);
    }

    // This button allows user to directly comment without looking at other people's comment.

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
