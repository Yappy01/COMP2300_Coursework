package Controller;

import Models.Post;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;

public class InputBoxController {
    @FXML
    private GridPane gridPane;
    @FXML
    private Label inputLabel;
    @FXML
    private TextField inputText;

    public String getInputText() {
        Node node = getNodeFromGridPane(gridPane, 1, 0);
        if (node instanceof ComboBox) {
            ComboBox<String> cb = (ComboBox<String>) node;
            return cb.getValue();
        } else {
            return inputText.getText();
        }
    }

    public void configInputBox(String label, String text) throws IOException {
        inputLabel.setText(label + ": ");
        inputText.setPromptText("input " + label + " here...");
        Node node = getNodeFromGridPane(gridPane, 1, 0);
        if (node instanceof ComboBox) {
            ComboBox<String> cb = (ComboBox<String>) node;
            cb.setValue(text);
        } else {
            inputText.setText(text);
        }
    }

    public void clearBox() {
        Node node = getNodeFromGridPane(gridPane, 1, 0);
        if (node instanceof ComboBox) {
            ComboBox<String> cb = (ComboBox<String>) node;
            cb.getSelectionModel().clearSelection();
        } else {
            inputText.clear();
        }
    }

    public void setMenuItem(ObservableList<String> list) {
        ComboBox<String> comboBox = new ComboBox<>(list);
        replaceNode(gridPane, comboBox, 1, 0);
    }

    private void replaceNode(GridPane grid, Node newNode, int col, int row) {
        // 1. Find the existing node to capture its current size
        for (Node node : grid.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            int currentCol = (nodeCol == null) ? 0 : nodeCol;
            int currentRow = (nodeRow == null) ? 0 : nodeRow;

            if (currentCol == col && currentRow == row) {
                break;
            }
        }

        // 2. Remove the existing node(s)
        grid.getChildren().removeIf(node -> {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            int currentCol = (nodeCol == null) ? 0 : nodeCol;
            int currentRow = (nodeRow == null) ? 0 : nodeRow;
            return currentCol == col && currentRow == row;
        });

        // 3. Apply the captured dimensions to the new node
        // Using Region allows us to set preferred sizes if newNode is a Control or Pane
        if (newNode instanceof Region) {
            Region region = (Region) newNode;
            region.setPrefSize(190, 32);
            region.setMaxSize(190, 32); // Ensures it doesn't grow larger than the original
        }

        // 4. Add the new one
        grid.add(newNode, col, row);
    }

    public Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            // Safe retrieval of constraints
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            // Treat null as 0 (JavaFX default behavior)
            int currentCol = (nodeCol == null) ? 0 : nodeCol;
            int currentRow = (nodeRow == null) ? 0 : nodeRow;

            if (currentCol == col && currentRow == row) {
                return node;
            }
        }
        return null; // No node found at those coordinates
    }
}
