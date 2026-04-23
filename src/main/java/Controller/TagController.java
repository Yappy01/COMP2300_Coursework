package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class TagController {

    private FlowPane tagArea;

    @FXML
    private Label tagLabel;

    public void setTagArea(FlowPane tagArea) {
        this.tagArea = tagArea;
    }

    public void setTagLabel(String tagName) {
        tagLabel.setText(tagName);
    }

    public String getTagLabel() {
        return tagLabel.getText();
    }

    public void removeClicked(ActionEvent event) {
        Node source = (Node) event.getSource();

        // Get the parent of the button (assuming that's the container you want to remove)
        Parent parent = source.getParent();

        tagArea.getChildren().remove(parent);
    }

}
