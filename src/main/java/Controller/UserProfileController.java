package Controller;

import DBHandling.EventDatabase;
import DBHandling.UserRepository;
import Models.UserEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserProfileController {

    @FXML private ToggleButton userPostButton;
    @FXML private ToggleGroup informationPageButtons;
    @FXML private ToggleButton informationpageButton;
    @FXML private Button username;
    @FXML private ToggleButton riskLevelButton;
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
        displayEventsFromDatabase();
        username_label.setText(Session.getInstance().getUserName());
    };

    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }


    @FXML //go to the personal information toggle page
    public void gotoPIPage(ActionEvent event) {
        initialize();
    }

    @FXML //go to the post page toggle page
    public void postSearch(ActionEvent event) {
    }

    @FXML void addPhoneNumber(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (pntextfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter phone number.");
            alert.showAndWait();

        }else{
            System.out.println();
            if(userRepo.change_phonenumber(Session.getInstance().getUserID(), pntextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Phone number was not updated.");
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

    @FXML public void addDateOfBirth(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (dateOfBirthField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter allergy name.");
            alert.showAndWait();

        }else{
            if(userRepo.change_date_of_birth(Session.getInstance().getUserID(), allergies_textfield.getText())){
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

    @FXML
    public void add_allergy() throws SQLException, ClassNotFoundException {
        if (allergies_textfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter allergy name.");
            alert.showAndWait();

        }else{
            if(userRepo.change_allergies(Session.getInstance().getUserID(), allergies_textfield.getText())){
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

    @FXML
    public void addchronicdisease(ActionEvent event ) throws SQLException, ClassNotFoundException {
        if (chronicdiseaseTextfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter chronic disease.");
            alert.showAndWait();

        }else{
            if(userRepo.change_cd(Session.getInstance().getUserID(), chronicdiseaseTextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Chronic Disease list was not updated.");
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

    @FXML
    public void addbt(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (btTextefield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your blood type.");
            alert.showAndWait();

        }else{
            if(userRepo.change_blood_type(Session.getInstance().getUserID(), chronicdiseaseTextfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Blood Type not updated.");
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

    @FXML
    public void addpiField() throws SQLException, ClassNotFoundException {
        if (piTextfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter pass injuries and illnesses.");
            alert.showAndWait();

        }else{
            if(userRepo.change_injuries_illness(Session.getInstance().getUserID(),chronicdiseaseTextfield.getText())){
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


    public void displayEventsFromDatabase() {
        System.out.println("populating list of events");

        eventContainer.getChildren().clear();

        List<UserEvent> userEvents = eventDatabase.getAllEvents();

        try {
            for (UserEvent userEvent : userEvents) {
                System.out.println("trying to load event");
                // Adding "/App/" before the filename
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/EventBox.fxml"));
                Node node = loader.load();

                //Access the controller of the specific EventBox to set its text
                EventBoxController controller = loader.getController();
                controller.setEventData(userEvent.getDate(), userEvent.getTime(), userEvent.getTitle(), userEvent.getDescription());

                //Add the box to the main container
                eventContainer.getChildren().add(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
