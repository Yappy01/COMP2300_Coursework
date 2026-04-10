package Controller;

import Models.Post;
import Service.PostService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.General;

import java.io.File;


public class CardController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label likeNumLabel;
    @FXML
    private Label commentNumLabel;
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

    private Post post;
    private final PostService postService = new PostService();

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    // Use this to set the data and contain of each card.
    public void setData(String name, String content, String date, Integer likeCount, Integer commentCount, String filePath) {
        this.nameLabel.setText(name);
        this.contentLabel.setText(content);
        this.dateLabel.setText(date);
        this.likeNumLabel.setText(General.formatLikes(likeCount));
        this.commentNumLabel.setText(General.formatLikes(commentCount));

        if (!filePath.equals("")) {
            System.out.println(filePath);
            Image image = new Image(filePath);
            this.contentImage.setImage(image);
            contentImage.setVisible(true);
            contentImage.setManaged(true);
        }
    }

    // View the entire content of the card / Bringing a small box up.
    public void cardClicked() {
        parentController.setOverlayVisibility(true);
        comPageOverlayController.setOverlayData(this.nameLabel.getText(), this.contentLabel.getText(), contentImage.getImage());
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

//    @FXML
//    private void initialize() {
//        // This detects if the data contains an image to showcase allowing it to change between text and image.
//        if (contentImage.getImage() == null) {
//            contentImage.setVisible(false);
//            contentImage.setManaged(false);
//        } else  {
//            System.out.println("visible");
//            contentImage.setVisible(true);
//            contentImage.setManaged(true);
//        }
//    }
}
