package Controller;

import Models.UserEvent;
import Service.EventService;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.General;
import utils.Session;

import java.io.IOException;
import java.sql.SQLException;

public class UserProfileController {

    @FXML private ToggleGroup visitsTreatments;
    @FXML private TextField pntextfield;
    @FXML private TextField dateOfBirthField;
    @FXML private Label genderField;
    @FXML private ToggleGroup genderGroup;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private RadioButton otherRadio;
    @FXML private TextField otherGenderField;
    @FXML private TextField allergies_textfield;
    @FXML private TextField chronicdiseaseTextfield;
    @FXML private TextField btTextefield;
    @FXML private TextField piTextfield;
    @FXML private VBox eventContainer;
    @FXML private ProgressIndicator progressIndicator;


    private final UserService userService = new UserService();
    private final EventService eventService = new EventService();

    @FXML private Label username_label;

    public void setProgressIndicatorVisible(Boolean value) {
        progressIndicator.setVisible(value);
    }

    @FXML
    public void initialize(){
        System.out.println("UserProfileController initialize");
        progressIndicator.setVisible(false);
        populateProfileFields();

        visitsTreatments.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                filterEvents();
            }
        });

        genderGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            otherGenderField.setVisible(newT == otherRadio);
            if (newT != otherRadio) otherGenderField.clear();
        });

        populateProfileFields();

        filterEvents();
        displayEventsFromDatabase();
        username_label.setText(Session.getInstance().getUserName());
    }

    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }


    @FXML //go to the personal information toggle page
    public void gotoPIPage(ActionEvent event) {
        initialize();
    }

    @FXML //go to the post page toggle page, not completed
    public void postSearch(ActionEvent event) {
    }

    public void populateProfileFields() {
        progressIndicator.setVisible(true);
        userService.getUserFullProfileAsync(Session.getInstance().getUserID(), (data) -> {
            if (!data.isEmpty()) {
                pntextfield.setText(data.getOrDefault("phone", ""));
                dateOfBirthField.setText(data.getOrDefault("dob", ""));

                // Set RadioButton based on DB value
                String gender = data.getOrDefault("gender", "");
                if ("Male".equalsIgnoreCase(gender)) {
                    genderField.setText("Male");
                    maleRadio.setSelected(true);
                } else if ("Female".equalsIgnoreCase(gender)) {
                    genderField.setText("Female");
                    femaleRadio.setSelected(true);
                } else if (!gender.isEmpty()) {
                    otherRadio.setSelected(true);
                    otherGenderField.setText("");
                    otherGenderField.setVisible(true);
                    genderField.setText(gender);
                }else if (gender == null || gender.trim().isEmpty()) {
                    genderField.setText("Not Specified");
                } else {
                    genderField.setText(gender);
                }
            }

            allergies_textfield.setText(data.getOrDefault("allergies", ""));
            chronicdiseaseTextfield.setText(data.getOrDefault("chronic", ""));
            btTextefield.setText(data.getOrDefault("blood", ""));
            piTextfield.setText(data.getOrDefault("injuries", ""));

            progressIndicator.setVisible(false);
        }, (error) -> {
            error.printStackTrace();
            progressIndicator.setVisible(false); });
    }

//    public void populateProfileFields() {
//        progressIndicator.setVisible(true);
//        userService.getUserFullProfileAsync(Session.getInstance().getUserID(), (data) -> {
//            if (!data.isEmpty()) {
//                // Use setText() for each respective field
//                pntextfield.setText(data.getOrDefault("phone", ""));
//                dateOfBirthField.setText(data.getOrDefault("dob", ""));
//                allergies_textfield.setText(data.getOrDefault("allergies", ""));
//                chronicdiseaseTextfield.setText(data.getOrDefault("chronic", ""));
//                btTextefield.setText(data.getOrDefault("blood", ""));
//                piTextfield.setText(data.getOrDefault("injuries", ""));
//            }
//            progressIndicator.setVisible(false);
//        }, (error) -> {
//            progressIndicator.setVisible(false);
//            error.printStackTrace();
//        });
//    }

    @FXML
    public void add_personalInformation() {
        progressIndicator.setVisible(true);

        // Get selected gender text
        String selectedGender = "";
        if (maleRadio.isSelected()) selectedGender = "Male";
        else if (femaleRadio.isSelected()) selectedGender = "Female";
        else if (otherRadio.isSelected()) selectedGender = otherGenderField.getText();

        userService.change_personalInformationAsync(
                Session.getInstance().getUserID(),
                pntextfield.getText(),
                dateOfBirthField.getText(),
                selectedGender,
                (value) -> {
                    if (value) General.getInfoAlert("Information successfully added.");
                    else General.getErrorAlert("Something went wrong");
                    progressIndicator.setVisible(false);
                    populateProfileFields();
                },
                (error) -> {
                    error.printStackTrace();
                    progressIndicator.setVisible(false);
                }
        );
    }

//    @FXML
//    public void add_personalInformation() throws SQLException, ClassNotFoundException {
//        progressIndicator.setVisible(true);
//        userService.change_personalInformationAsync(Session.getInstance().getUserID(), pntextfield.getText(),dateOfBirthField.getText(), (value) -> {
//            if (value){
//                General.getInfoAlert("Information successfully added.");
//            }else{
//                General.getErrorAlert("Something went wrong");
//            }
//            progressIndicator.setVisible(false);
//        }, (error) -> {
//            error.printStackTrace();
//            progressIndicator.setVisible(false);
//        });
//    }



    @FXML void add_anamnesis() throws SQLException, ClassNotFoundException {
        progressIndicator.setVisible(true);
        userService.change_anamnesisAsync(Session.getInstance().getUserID(),allergies_textfield.getText(), chronicdiseaseTextfield.getText(), btTextefield.getText(), piTextfield.getText(),
                (value) -> {
                    if (value){
                        General.getInfoAlert("Information successfully added.");
                    }else{
                        General.getErrorAlert("Something went wrong");
                    }
                    progressIndicator.setVisible(false);
                }, (error) -> {
                    error.printStackTrace();
                    progressIndicator.setVisible(false);
                });
    }

    //display all events unfiltered in database
    public void displayEventsFromDatabase() {
        System.out.println("populating list of events");
        progressIndicator.setVisible(true);
        eventContainer.getChildren().clear();
        UserProfileController currentController = this;
        eventService.getAllEventsAsync(
                (userEvents) -> {
                    progressIndicator.setVisible(false);
                    try {
                        for (UserEvent userEvent : userEvents) {
                            System.out.println("trying to load event");
                            // Adding "/App/" before the filename
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/eventBox.fxml"));
                            Node node = loader.load();

                            //Access the controller of the specific EventBox to set its text
                            EventBoxController controller = loader.getController();
                            controller.setEventData(userEvent.getDate(), userEvent.getTime(), userEvent.getTitle(), userEvent.getDescription());
                            controller.setUserProfileController(currentController);

                            //Add the box to the main container
                            eventContainer.getChildren().add(node);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                },
                (error) -> {
                    error.printStackTrace();
                    progressIndicator.setVisible(false);
                }
        );
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
        UserProfileController currentController = this;
        eventContainer.getChildren().clear();
        progressIndicator.setVisible(true);
        eventService.getFilteredEventsAsync(Session.getInstance().getUserID(), typeId, timeMode,
            (filtered) -> {
                progressIndicator.setVisible(false);
                for (UserEvent userEvent : filtered) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/eventBox.fxml"));
                        Node node = loader.load();

                        EventBoxController controller = loader.getController();
                        controller.setEventData(userEvent.getDate(), userEvent.getTime(), userEvent.getTitle(), userEvent.getDescription());
                        controller.setUserProfileController(currentController);

                        eventContainer.getChildren().add(node);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }, (error) -> {
            error.printStackTrace();
            progressIndicator.setVisible(false);
            });
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/components/calendar.fxml"));
            Parent root = loader.load();

            CalendarController calendarController = loader.getController();
            calendarController.setUserProfileController(this);

            // Type ID based on selection
            ToggleButton selected = (ToggleButton) visitsTreatments.getSelectedToggle();
            int typeToCreate = 1; // Default to Visit

            if (selected != null && selected.getText().equals("PLANNED TREATMENTS")) {
                typeToCreate = 2;
            }

            System.out.println("typeToCreate: " + typeToCreate);

            calendarController.setInitialType(typeToCreate);
            calendarController.setUserProfileController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void signOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                HomePageController.class.getResource("/fxml/pages/LRFDocument_updated.fxml")
        );

        Stage stage = (Stage) pntextfield.getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }
}
