package Controller;

import DBHandling.EventDatabase;
import Models.User;
import Models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private TextField eventTitle;
    @FXML private TextField eventDescription;
    @FXML private ComboBox<String> hourBox;
    @FXML private ComboBox<String> minuteBox;
    @FXML private ToggleGroup amPmGroup;
    @FXML private RadioButton pmRadio;

    private final EventDatabase eventDatabase = new EventDatabase();
    private UserProfileController userProfileController;

    private int selectedTypeId = 1;

    public void setInitialType(int typeId) {
        this.selectedTypeId = typeId;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datePicker.setShowWeekNumbers(false);

        // Populate Time Dropdowns
        for (int i = 1; i <= 12; i++) hourBox.getItems().add(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minuteBox.getItems().add(String.format("%02d", i));

        datePicker.setValue(LocalDate.now());
    }

    @FXML
    public void addEvent() {
        // Get Date
        LocalDate date = datePicker.getValue();
        // Validate Inputs
        if (date == null || hourBox.getValue() == null || minuteBox.getValue() == null || eventTitle.getText().isEmpty()) {
            System.out.println("Error: Please fill all fields.");
            return;
        }

        try {
            //Time converted to 24hr format
            int hour = Integer.parseInt(hourBox.getValue());
            int minute = Integer.parseInt(minuteBox.getValue());
            boolean isPM = pmRadio.isSelected();

            if (isPM && hour < 12) hour += 12;
            else if (!isPM && hour == 12) hour = 0;

            //Create LocalDateTime then convert to SQL Timestamp
            LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(hour, minute));
            Timestamp sqlTimestamp = Timestamp.valueOf(ldt);
            System.out.println(ldt);

            // Save to database
            eventDatabase.saveEvent(
                    eventTitle.getText(),
                    eventDescription.getText(),
                    sqlTimestamp,
                    UserSession.getInstance().getUserId(),
                    selectedTypeId
            );

            clearFields();
            System.out.println("Event Saved Successfully!");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Event Saved!");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        eventTitle.clear();
        eventDescription.clear();
        hourBox.getSelectionModel().clearSelection();
        minuteBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        if (userProfileController != null) {
            userProfileController.refreshEvents();
        }
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }



}