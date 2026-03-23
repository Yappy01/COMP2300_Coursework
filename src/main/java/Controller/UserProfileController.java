package Controller;

import DBHandling.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

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

    private final UserRepository userRepo = new UserRepository();

    @FXML private Label username_label;


    @FXML
    public void initialize(String username){
        username_label.setText(username);
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
            if(userRepo.change_phonenumber(username_label.getText(), allergies_textfield.getText())){
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
            if(userRepo.change_date_of_birth(username_label.getText(), allergies_textfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Date of Birth was not updated.");
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
    public void add_allergy() throws SQLException, ClassNotFoundException {
        if (allergies_textfield.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter allergy name.");
            alert.showAndWait();

        }else{
            if(userRepo.change_allergies(username_label.getText(), allergies_textfield.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Allergy not stored.");
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
            if(userRepo.change_cd(username_label.getText(), chronicdiseaseTextfield.getText())){
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
            if(userRepo.change_blood_type(username_label.getText(), chronicdiseaseTextfield.getText())){
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
            if(userRepo.change_injuries_illness(username_label.getText(), chronicdiseaseTextfield.getText())){
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

}
