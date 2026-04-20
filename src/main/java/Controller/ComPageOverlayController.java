package Controller;

import Models.Post;
import Service.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.General;

import java.io.IOException;

public class ComPageOverlayController {

    @FXML
    private Label overlayUsernameLabel;
    @FXML
    private Label overlayContentLabel;
    @FXML
    private TextField commentTextField;
    @FXML
    private ImageView imageArea;
    @FXML
    private PostParent parentController;
    @FXML
    private VBox commentBox;

    private Post post;
    private final PostService postService = new PostService();

    public void setParentController(PostParent parentController) {
        this.parentController = parentController;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setOverlayData(String name, String content, Image image) {
        overlayUsernameLabel.setText(name);
        overlayContentLabel.setText(content);
        if (image != null) {
            this.imageArea.setImage(image);
            imageArea.setVisible(true);
            imageArea.setManaged(true);
        }
    }

    public void clickExitButton() {
        parentController.setOverlayVisibility(false);
        commentBox.getChildren().clear();
        imageArea.setImage(null);
    }

    public void likeClicked() {
        parentController.setProgressIndicatorVisibility(true);
        postService.likePost(post, (value) -> {
            parentController.setProgressIndicatorVisibility(false);
        }, (error) -> {
            parentController.setProgressIndicatorVisibility(false);
            error.printStackTrace();
        });
    }

    public void sendComment() {
        postService.commentPost(post, commentTextField.getText());
        commentTextField.clear();
        setCommentSection();
    }

    public void setCommentSection() {
        parentController.setProgressIndicatorVisibility(true);
        commentBox.getChildren().clear();
        postService.getCommentsAsync(post, (comments) -> {
            for (int i = 0; i < comments.size(); i++) {
                comments.get(i);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/comment.fxml"));
                    Node comment = loader.load();

                    CommentController commentController = loader.getController();
                    commentController.setComment(comments.get(i).getContent());
                    commentBox.getChildren().add(comment);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            parentController.setProgressIndicatorVisibility(false);
        }, (error) -> {
            error.printStackTrace();
            parentController.setProgressIndicatorVisibility(false);
        });
    }
}
