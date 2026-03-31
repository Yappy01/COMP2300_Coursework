package Controller;

import DBHandling.EventDatabase;
import DBHandling.UserRepository;
import Models.UserSession;
import Models.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserProfileController {

    @FXML private ToggleGroup visitsTreatments;
    @FXML private TextField pntextfield;
    @FXML private TextField dateOfBirthField;
    @FXML private TextField allergies_textfield;
    @FXML private TextField chronicdiseaseTextfield;
    @FXML private TextField btTextefield;
    @FXML private TextField piTextfield;
    @FXML private VBox eventContainer;


    private final UserRepository userRepo = new UserRepository();
    private final EventDatabase eventDatabase = new EventDatabase();

    @FXML private Label username_label;


    @FXML
    public void initialize(){
        System.out.println("UserProfileController initialize");
        populateProfileFields();

        visitsTreatments.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                filterEvents();
            }
        });

        filterEvents();
        displayEventsFromDatabase();

        username_label.setText(UserSession.getInstance().getUserName());
    };

    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/App/homePage.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML //go to the personal information toggle page
    public void gotoPIPage(ActionEvent event) {
        initialize();
    }

    @FXML //go to the post page toggle page, not completed
    public void postSearch(ActionEvent event) {
    }

    public void populateProfileFields() {
        Map<String, String> data = userRepo.getUserFullProfile(UserSession.getInstance().getUserId());

        if (!data.isEmpty()) {
            // Use setText() for each respective field
            pntextfield.setText(data.getOrDefault("phone", ""));
            dateOfBirthField.setText(data.getOrDefault("dob", ""));
            allergies_textfield.setText(data.getOrDefault("allergies", ""));
            chronicdiseaseTextfield.setText(data.getOrDefault("chronic", ""));
            btTextefield.setText(data.getOrDefault("blood", ""));
            piTextfield.setText(data.getOrDefault("injuries", ""));
        }
    }

    //add phone number to database
    @FXML void addPhoneNumber(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (pntextfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter phone number.");
            alert.showAndWait();

        }else{
            System.out.println();
            if(userRepo.change_phonenumber(UserSession.getInstance().getUserId(), pntextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Phone number was updated.");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        }
    }

    //add date of birth to database
    @FXML public void addDateOfBirth(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (dateOfBirthField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter allergy name.");
            alert.showAndWait();

        }else{
            if(userRepo.change_date_of_birth(UserSession.getInstance().getUserId(), dateOfBirthField.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Date of Birth was updated.");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Date of Birth was not updated.");
                alert.showAndWait();
            }
        }
    }

    //add allergy to database
    @FXML
    public void add_allergy() throws SQLException, ClassNotFoundException {
        if (allergies_textfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter allergy name.");
            alert.showAndWait();

        }else{
            if(userRepo.change_allergies(UserSession.getInstance().getUserId(), allergies_textfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Allergy stored.");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        }

    }

    //add chronic diseases in the database
    @FXML
    public void addchronicdisease(ActionEvent event ) throws SQLException, ClassNotFoundException {
        if (chronicdiseaseTextfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter chronic disease.");
            alert.showAndWait();

        }else{
            if(userRepo.change_cd(UserSession.getInstance().getUserId(), chronicdiseaseTextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Chronic Disease list was updated.");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        }

    }


    //add blood type to database
    @FXML
    public void addbt(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (btTextefield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your blood type.");
            alert.showAndWait();

        }else{
            if(userRepo.change_blood_type(UserSession.getInstance().getUserId(), chronicdiseaseTextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Blood Type was updated.");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        }
    }

    //add past in daatabase
    @FXML
    public void addpiField() throws SQLException, ClassNotFoundException {
        if (piTextfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter pass injuries and illnesses.");
            alert.showAndWait();

        }else{
            if(userRepo.change_injuries_illness(UserSession.getInstance().getUserId(),chronicdiseaseTextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Illness and Injuries was not updated.");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        }
    }


    //display all events unfiltered in database
    public void displayEventsFromDatabase() {
        System.out.println("populating list of events");

        eventContainer.getChildren().clear();

        List<Event> events = eventDatabase.getAllEvents();

        try {
            for (Event event : events) {
                System.out.println("trying to load event");
                // Adding "/App/" before the filename
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/EventBox.fxml"));
                Node node = loader.load();

                //Access the controller of the specific EventBox to set its text
                EventBoxController controller = loader.getController();
                controller.setEventData(event.getDate(), event.getTime(), event.getTitle(), event.getDescription());
                controller.setUserProfileController(this);

                //Add the box to the main container
                eventContainer.getChildren().add(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //filter events as per future visits, past visits, planned treatments
    private void filterEvents() {
        ToggleButton selected = (ToggleButton) visitsTreatments.getSelectedToggle();
        if (selected == null) return;

        String text = selected.getText();
        int typeId = 1;      // Default for Visits
        String timeMode = "";

        //Based on buttons, future visits, past visits, planned treatments
        if (text.equals("FUTURE VISITS")) {
            typeId = 1;
            timeMode = "FUTURE";
        } else if (text.equals("PAST VISITS")) {
            typeId = 1;
            timeMode = "PAST";
        } else if (text.equals("PLANNED TREATMENTS")) {
            typeId = 2;
            timeMode = "ALL"; // Or specify FUTURE if needed
        }

        updateUI(typeId, timeMode);
    }

    //updateUI when events are filtered
    private void updateUI(int typeId, String timeMode) {
        eventContainer.getChildren().clear();

        List<Event> filtered = eventDatabase.getFilteredEvents(UserSession.getInstance().getUserId(), typeId, timeMode);

        for (Event event : filtered) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/EventBox.fxml"));
                Node node = loader.load();

                EventBoxController controller = loader.getController();
                controller.setEventData(event.getDate(), event.getTime(), event.getTitle(), event.getDescription());
                controller.setUserProfileController(this);

                eventContainer.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void refreshEvents() {
        if (eventContainer == null) {
            System.err.println("Critical Error: eventContainer is null! Check FXML fx:id.");
            return;
        }
        eventContainer.getChildren().clear();
        displayEventsFromDatabase();
    }


    @FXML
    private void openAddEventWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Calendar.fxml"));
            Parent root = loader.load();

            CalendarController calendarController = loader.getController();

            // Type ID based on selection
            ToggleButton selected = (ToggleButton) visitsTreatments.getSelectedToggle();
            int typeToCreate = 1; // Default to Visit

            if (selected != null && selected.getText().equals("PLANNED TREATMENTS")) {
                typeToCreate = 2;
            }

            System.out.println("typeToCreate: " + typeToCreate);

            calendarController.setInitialType(typeToCreate);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
