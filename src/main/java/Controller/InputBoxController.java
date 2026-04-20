package Controller;

import Models.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class InputBoxController {
    @FXML
    private Label inputLabel;
    @FXML
    private TextField inputText;

    public String getInputText() {
        return inputText.getText();
    }

    public void configInputBox(String label, String text) throws IOException {
        inputLabel.setText(label + ": ");
        inputText.setPromptText("input " + label + " here...");
        inputText.setText(text);
    }

    public void clearBox() {
        inputText.clear();
    }
}
