package Controller;

import DBHandling.UserRepository;
import Models.Comment;
import Models.Post;
import Service.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.smartcardio.Card;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ComPageOverlayController {

    @FXML
    public Label overlayUsernameLabel;
    @FXML
    public Label overlayContentLabel;
    @FXML
    public TextField commentTextField;
    @FXML
    private CommunityPageController parentController;
    @FXML
    private VBox commentBox;

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
        commentBox.getChildren().clear();
    }

    public void likeClicked() {
        PostService.likePost(post);
    }

    public void sendComment() {
        PostService.commentPost(post, commentTextField.getText());
        commentTextField.clear();
    }

    public void setCommentSection() {
        ArrayList<Comment> comments = PostService.getComments(post);
        System.out.println("COMMENTS: " + comments.size());
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

    }
}
