package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class FileLabelController {
    @FXML
    private Label imageLabel;

    @FXML
    private VBox fileLabelArea;

    private ImageView imagePreview;
    private Button uploadButton;

    public void setUploadButton(Button uploadButton) {
        this.uploadButton = uploadButton;
    }

    public void setImagePreview(ImageView imagePreview) {
        this.imagePreview = imagePreview;
    }

    public void setFileLabelArea(VBox fileLabelArea) {
        this.fileLabelArea = fileLabelArea;
    }

    public void setImageLabel(String filePath) {
        this.imageLabel.setText(filePath);
    }

    @FXML
    public void removeClicked(ActionEvent event) {
        Node source = (Node) event.getSource();

        // Get the parent of the button (assuming that's the container you want to remove)
        Parent parent = source.getParent();

        fileLabelArea.getChildren().remove(parent);
        uploadButton.setVisible(true);
        imagePreview.setImage(null);
    }
}
