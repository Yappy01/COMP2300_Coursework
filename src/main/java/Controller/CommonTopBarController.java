package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CommonTopBarController {
    @FXML
    private Label pageTitle;
    @FXML
    private Button userPageBtn;
    @FXML
    public void userPage(ActionEvent event) {

    }
    public void setUp(String title, String username) {
        pageTitle.setText(title);
        userPageBtn.setText(username);
    }
}
