package Controller;

import DBHandling.EventDatabase;
import Models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class EventBoxController {
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;

    @FXML private final EventDatabase eventDatabase = new EventDatabase();
     private UserProfileController userProfileController;

     public void setUserProfileController(UserProfileController userProfileController) {
         this.userProfileController = userProfileController;
     }

    public void setEventData(String date, String time, String title, String description) {
        dateLabel.setText(date);
        timeLabel.setText(time);
        titleLabel.setText(title);
        descriptionLabel.setText(description);
    }


    @FXML
    public void deleteEvent(MouseEvent mouseEvent) {
        eventDatabase.deleteEvent(titleLabel.getText(), UserSession.getInstance().getUserId());
        if (userProfileController != null) {
            userProfileController.refreshEvents();
        }
    }
}