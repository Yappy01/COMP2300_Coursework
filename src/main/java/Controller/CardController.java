package Controller;

import Models.Post;
import Models.User;
import Service.PostService;
import Service.UserService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.General;
import utils.Session;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


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
    private ButtonBar buttonBar;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private PostParent parentController;

    private ComPageOverlayController comPageOverlayController;
    private OverlayBController overlayBController;

    private Post post;
    private final PostService postService = new PostService();
    private final UserService userService = new UserService();

    public void setParentController(PostParent parentController) {
        this.parentController = parentController;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    // Use this to set the data and contain of each card.
    public void setData(String name, String content, Timestamp date, Integer likeCount, Integer commentCount, String filePath) {
        if (post == null) {
            return;
        }
        if (post.getReasonDeleted() != null) {
            contentLabel.setText("Post Deleted: " + post.getReasonDeleted());
            editButton.setVisible(false);
            return;
        }

        if (userService.searchByUsername(name).getRole().equals("Verified")) {
            name += "✅";
        }

        this.nameLabel.setText(name);
        this.contentLabel.setText(content);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        String tsString = sdf.format(post.getCreatedAt());

        this.dateLabel.setText(tsString);
        this.likeNumLabel.setText(General.formatLikes(likeCount));
        this.commentNumLabel.setText(General.formatLikes(commentCount));

        if (parentController instanceof CommunityPageController) {
            buttonBar.setVisible(false);
        } else if (parentController instanceof AdminMainController) {
            editButton.setVisible(false);
        }
        if (!filePath.equals("")) {
            System.out.println(filePath);
            Image image = new Image(filePath);
            this.contentImage.setImage(image);
            contentImage.setVisible(true);
            contentImage.setManaged(true);
        }
        deleteButton.setOnAction(e -> {
            parentController.setProgressIndicatorVisibility(true);

            if (parentController instanceof AdminMainController) {
                String text = General.getTextInput("Deletion of user post", "Please Type Reason for Deletion: ");
                postService.tempDeleteAsync(post.getPostId(), text,(value) -> {
                    if (value) {
                        System.out.println("Successfully deleted");
                    }
                    parentController.setProgressIndicatorVisibility(false);
                    parentController.reloadCards();
                }, (error) -> {
                    error.printStackTrace();
                    parentController.setProgressIndicatorVisibility(false);
                });
            } else {
                postService.deletePostAsync(post, (value) -> {
                    if (value) {
                        System.out.println("Successfully deleted");
                    }
                    parentController.setProgressIndicatorVisibility(false);
                    parentController.reloadCards();
                }, (error) -> {
                    error.printStackTrace();
                    parentController.setProgressIndicatorVisibility(false);
                });
            }
        });

        editButton.setOnAction(e -> {
            parentController.setOverlayBVisibility(true);
            overlayBController.setOriPost(post);
            overlayBController.setData();
        });
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
        parentController.setProgressIndicatorVisibility(true);
        postService.likePost(post, (value) -> {
            parentController.setProgressIndicatorVisibility(false);
            if (value) {
                likeNumLabel.setText(General.formatLikes(Integer.valueOf(likeNumLabel.getText()) + 1));
            }
        }, (error) -> {
            parentController.setProgressIndicatorVisibility(false);
            error.printStackTrace();
        });
    }

    // This button allows user to directly comment without looking at other people's comment.

    public void setComPageOverlayController(ComPageOverlayController comPageOverlayController) {
        this.comPageOverlayController = comPageOverlayController;
    }

    public void setOverlayBController(OverlayBController overlayBController) {
        this.overlayBController = overlayBController;
    }
}
