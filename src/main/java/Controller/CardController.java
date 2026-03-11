package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class CardController {
    @FXML
    private Label name;
    @FXML
    private Label content;
    @FXML
    private Label date;
    @FXML
    private Button card;
    @FXML
    private Button like;
    @FXML
    private Button comment;

    public void setData(String name, String content, String date) {
        this.name.setText(name);
        this.content.setText(content);
        this.date.setText(date);
    }
}
