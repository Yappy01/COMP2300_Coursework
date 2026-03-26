package Controller;

import DBHandling.ComPostDatabase;
import Models.Post;
import Models.User;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class OverlayBController {
    @FXML
    private CommunityPageController parentController;

    @FXML
    private TextArea postText;

    @FXML
    private VBox imageArea;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Button uploadButton;

    private String imageLink = "";

    public void setParentController(CommunityPageController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void clickExitButton() {
        parentController.setOverlayBVisibility(false);
        imagePreview.setVisible(false);
        imagePreview.setManaged(false);
        uploadButton.setVisible(true);
        postText.clear();
    }

    private File selectedFile; // store the chosen file temporarily

    @FXML
    public void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Just select the file; don't copy yet
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
        String savedPath = null;

        if (selectedFile != null) {
            try {
                // Create uploads folder if it doesn't exist
                Path uploadsDir = Paths.get("uploads/images");
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                }

                // Generate a unique filename
                String extension = selectedFile.getName()
                        .substring(selectedFile.getName().lastIndexOf("."));
                String uniqueName = UUID.randomUUID().toString() + extension;

                Path destination = uploadsDir.resolve(uniqueName);

                // Copy file now
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                savedPath = destination.toString();

                System.out.println("File saved to: " + savedPath);
                uploadButton.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save the file.");
            }
        }

        // Create post with optional image path
        Post post = new Post(user.getUserId(), postText.getText(), savedPath);
        ComPostDatabase.insertPost(post);

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
