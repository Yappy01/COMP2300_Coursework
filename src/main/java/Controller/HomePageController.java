package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import DBHandling.UserRepository;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.EventObject;
import java.util.Objects;

public class HomePageController {
    //all textfields, passwordfields and button in the fxml has been recorded here
    //even if they are not all used

    @FXML private Button mp_UserPageBtn;

    @FXML private Button mp_InfoBtn;
    @FXML private Button mp_QuizBtn;
    @FXML private Button mp_ComBtn;

    @FXML private Button mp_TodayFactBtn;
    @FXML private Button mp_TodayQuizBtn;

    @FXML private TextField mp_NoteToSelf;

    private final UserRepository userRepo = new UserRepository();
    private Alert alert;

    @FXML private StackPane homePagePane;

    //initiate the homePagePane
    @FXML
    public void init_mainpage() {
        homePagePane.setVisible(true);
    }

    //assign the username to the usertag on top right
    public void assign_usertag(String user) {
        mp_UserPageBtn.setText(user);
    }

    //access to userpage, Connected, to be tested
    @FXML
    public void userpage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfile.fxml"));

        Parent root = loader.load();

        UserProfileController controller = loader.getController();
        controller.initialize(mp_UserPageBtn.getText());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    //access to information Page, Connected
    @FXML
    public void infoBtn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/App/information.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

    //access to quiz Page, not yet made
    @FXML
    public void quizBtn() {
        homePagePane.setVisible(false);
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("The quiz page will be displayed.");
        alert.showAndWait();
        init_mainpage();

    }

    //access to community page
    @FXML
    public void comBtn() {
        homePagePane.setVisible(false);
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("The community page will be displayed.");
        alert.showAndWait();
        init_mainpage();
    }

    //access to today's fact, not yet connected
    @FXML
    public void todayFactBtn() {
        homePagePane.setVisible(false);
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("The today fact in information page will be displayed.");
        alert.showAndWait();
        init_mainpage();
    }

    //access to today's quiz, not yet made
    @FXML
    public void todayQuizBtn() {
        homePagePane.setVisible(false);
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("The today quiz page will be displayed.");
        alert.showAndWait();
        init_mainpage();
    }

    //when user types in notetoself textfield
    @FXML
    public void noteToSelfBtn() {
        String name = mp_UserPageBtn.getText();
        String note = mp_NoteToSelf.getText();

        try{
            //change the note in the database
            if(userRepo.change_notetoself(name,note)){
               setNoteToSelf();
            }else{
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("The note was not changed.");
                alert.showAndWait();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //fetch the note from the database
    public void setNoteToSelf() {
        try{
            String name = mp_UserPageBtn.getText();
            String note = userRepo.fetch_notetoself(name);

            if (Objects.equals(note, null)) {
                mp_NoteToSelf.clear();
            }else{
                mp_NoteToSelf.setText(note);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
