package Controller;

import Models.Post;
import Models.User;
import Service.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import utils.General;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.UUID;

public class OverlayBController {
    @FXML
    private PostParent parentController;

    @FXML
    private TextArea postText;

    @FXML
    private VBox imageArea;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Button uploadButton;

    @FXML
    private Button insertButton;

    private final PostService postService = new PostService();

    private File selectedFile; // store the chosen file temporarily

    private Post oriPost;

    public void setOriPost(Post oriPost) {
        this.oriPost = oriPost;
    }

    public void setParentController(PostParent parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void clickExitButton() {
        parentController.setOverlayBVisibility(false);
        imagePreview.setVisible(false);
        imagePreview.setManaged(false);
        uploadButton.setVisible(true);
        imageArea.getChildren().clear();
        postText.clear();
        insertButton.setOnAction(e -> {
            insertPost();
        });
    }

    public void setData() {
        postText.setText(oriPost.getContent());
        try {
            FXMLLoader fileLabelLoader = new FXMLLoader(getClass().getResource("/fxml/components/fileLabel.fxml"));
            Node fileLabel = fileLabelLoader.load();
            FileLabelController controller = fileLabelLoader.getController();
            controller.setImageLabel(oriPost.getImageLink());
            controller.setFileLabelArea(imageArea);
            controller.setUploadButton(uploadButton);
            controller.setImagePreview(imagePreview);
            insertButton.setText("edit");
            insertButton.setOnAction(e -> {
                System.out.println("SUCCESSFULLY EDITED");
                editPost();
            });

            Image image = new Image(oriPost.getImageLink());
            imagePreview.setVisible(true);
            imagePreview.setManaged(true);
            imagePreview.setImage(image);
            imageArea.getChildren().add(fileLabel);
            uploadButton.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load file label UI.");
        }
    }

    @FXML
    public void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedFile = fileChooser.showOpenDialog(parentController.getAddPostPage().getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Load UI for the selected file
                FXMLLoader fileLabelLoader = new FXMLLoader(getClass().getResource("/fxml/components/fileLabel.fxml"));
                Node fileLabel = fileLabelLoader.load();
                FileLabelController controller = fileLabelLoader.getController();
                controller.setImageLabel(selectedFile.getAbsolutePath());
                controller.setFileLabelArea(imageArea);
                controller.setUploadButton(uploadButton);
                controller.setImagePreview(imagePreview);
                Image image = new Image(selectedFile.toURI().toString());
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);
                imagePreview.setImage(image);
                imageArea.getChildren().add(fileLabel);
                uploadButton.setVisible(false);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load file label UI.");
            }
        } else {
            System.out.println("No file selected.");
        }
    }

    @FXML
    public void insertPost() {
        User user = Session.getInstance().getUser();

        if (selectedFile != null) {
            uploadButton.setVisible(true);
        }

        // Create post with optional image path
        if (postText.getText().isEmpty() && selectedFile == null) {
            General.getErrorAlert("Cannot upload empty post");
            return ;
        }

        Post post = new Post(user.getUserId(), postText.getText(), "", "");
        postService.insertPostAsync(post, selectedFile,
            () -> {
                parentController.setProgressIndicatorVisibility(false);
                parentController.reloadCards();
            },
            (error) -> {
                parentController.setProgressIndicatorVisibility(false);
                error.printStackTrace();
            });

        // Clear temporary file reference after posting
        postText.clear();
        selectedFile = null;
        imageArea.getChildren().clear();
        clickExitButton();
    }

    @FXML
    public void editPost() {

        if (selectedFile != null) {
            uploadButton.setVisible(true);
        }

        // Create post with optional image path
        if (postText.getText().isEmpty() && selectedFile == null) {
            General.getErrorAlert("Cannot upload empty post");
            return ;
        }

        if (oriPost.getContent().equals(postText.getText()) && selectedFile == null) {
            return ;
        } else {
            oriPost.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            oriPost.setContent(postText.getText());
            postService.editPostAsync(oriPost, selectedFile,
                    (value) -> {
                        if (value) {
                            parentController.setProgressIndicatorVisibility(false);
                            parentController.reloadCards();
                        }
                    },
                    (error) -> {
                        parentController.setProgressIndicatorVisibility(false);
                        error.printStackTrace();
                    });
        }


        // Clear temporary file reference after posting
        postText.clear();
        selectedFile = null;
        imageArea.getChildren().clear();
        clickExitButton();
    }

    @FXML
    public void initialize() {
        if (imagePreview.getImage() == null) {
            imagePreview.setVisible(false);
            imagePreview.setManaged(false);
        } else  {
            System.out.println("visible");
            imagePreview.setVisible(true);
            imagePreview.setManaged(true);
        }
    }
}
