package Controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Timestamp;
import java.util.List;

public class EventBoxController {
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;

    public void setEventData(String date, String time, String title, String description) {
        dateLabel.setText(date);
        timeLabel.setText(time);
        titleLabel.setText(title);
        descriptionLabel.setText(description);
    }


}