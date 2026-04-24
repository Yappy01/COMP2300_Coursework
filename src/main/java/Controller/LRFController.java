package Controller;
import Models.User;
import Service.UserService;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.General;
import utils.Session;
import utils.UIConstant;

import java.sql.SQLException;


public class LRFController {
    @FXML private ProgressIndicator progressIndicator;
    //all textfields, passwordfields and button in the fxml has been recorded here
    //even if they are not all used
    @FXML private TextField si_username;
    @FXML private PasswordField si_password;


    @FXML private AnchorPane su_signupForm;
    @FXML private AnchorPane si_loginForm;


    @FXML private TextField su_username;
    @FXML private PasswordField su_password;
    @FXML private TextField su_email;
    @FXML private TextField su_answer;
    @FXML private ComboBox<String> su_question;
//    @FXML private TextField su_answer1;
    @FXML private Button su_signupBtn;

    @FXML private AnchorPane fp_questionForm;
    @FXML private Button fp_proceedBtn;
    @FXML private TextField fp_username;
    @FXML private TextField fp_email;
    @FXML private TextField fp_answer;
    @FXML private Button fp_back;
    @FXML private ComboBox<String> fp_question; //fp_questionForm
//    @FXML private TextField su_answer11; //fp_questionform

    @FXML private AnchorPane np_newPassForm;
    @FXML private PasswordField np_newPassword;
    @FXML private PasswordField np_confirmPassword;
    @FXML private Button np_changePassBtn;
    @FXML private Button np_back;

    @FXML private AnchorPane side_form;
    @FXML private Button side_CreateBtn;
    @FXML private Button side_alreadyHave;
    @FXML Hyperlink si_forgotPass;
    @FXML private Button si_loginBtn;

    private final UserService userService = new UserService();

    @FXML private void initialize() {
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);

        ObservableList<String> questions = FXCollections.observableArrayList(
                "What was your first pet's name?",
                "What street did you grow up on?",
                "What was your first car?",
                "What is your mother's maiden name?"
        );

        su_question.setItems(questions);
        fp_question.setItems(questions);

        su_answer.setEditable(false);
        fp_answer.setEditable(false);

        su_question.setOnShowing(event -> {
            su_answer.setEditable(true);

            General.getInfoAlert("Remember this question.\nIt will be asked to change your password");

            su_answer.requestFocus();
        });


        fp_question.setOnShowing(event -> {
            fp_answer.setEditable(true);
            General.getInfoAlert("Select the question selected during registration");

            fp_answer.requestFocus();
        });
    }

    @FXML
    public void loginBtn(){
        progressIndicator.setVisible(true);
        //if any of the textfields are empty
        if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {
            General.getErrorAlert("Please fill all blank fields");
        } else {
            String username = this.si_username.getText().trim();
            String password = this.si_password.getText().trim();

            userService.secureLoginAsync(username, password, (value) -> {
                progressIndicator.setVisible(false);
                try{
                    if (value){
                        //alert for login
                        General.getInfoAlert("Successfully Login!");

                        //call init, assign_usertag and setNoteToSelf
                        Session.startSession(userService.searchByUsername(si_username.getText()));

                        // Create loader pointing to HomePage FXML file
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pages/homepage.fxml"));
                        Parent root = loader.load();

                        //new Scene for HomePage
                        Scene scene = new Scene(root,UIConstant.main_width,UIConstant.main_height);

                        Stage stage = (Stage) si_username.getScene().getWindow();
                        stage.setScene(scene);
                        stage.centerOnScreen();
                        stage.show();
                    }else{
                        //Incorrect credentials alert
                        General.getErrorAlert("Incorrect Username/Password");
                    }
                }catch(Exception e1) {
                    e1.printStackTrace();
                }
            }, (error) -> {
                progressIndicator.setVisible(false);
                error.printStackTrace();
            });
        }
    }


    @FXML
    public void switchForgotPass() {
        //the side_form(Create Account is visible)
        side_form.setVisible(true);
        //remove the login form
        si_loginForm.setVisible(false);
        //make question form visible(email and username) to identify the account
        //prepare for proceedBtn
       fp_questionForm.setVisible(true);
    }


    @FXML
    public void regBtn(){
        String errorMsg = userService.validateCredentials(su_username.getText(), su_password.getText(), su_email.getText(),
                su_answer.getText(), su_question.getSelectionModel().getSelectedItem());
        if (errorMsg.isEmpty()) {
            try{
                String answer = this.su_answer.getText().trim();
                String username = this.su_username.getText();
                String password = this.su_password.getText();
                String email = this.su_email.getText();

                progressIndicator.setVisible(true);

                User user = new User(username,password,email,answer, "User");

                userService.register_userAsync(user, (value) -> {
                    try {
                        if(value){
                            General.getInfoAlert("Successfully registered Account!");
                            su_username.clear();
                            su_password.clear();
                            su_email.clear();

                            TranslateTransition slider = new TranslateTransition();

                            slider.setNode(side_form);
                            slider.setToX(0);
                            slider.setDuration(Duration.seconds(.5));

                            slider.setOnFinished((ActionEvent e1) -> {
                                side_alreadyHave.setVisible(false);
                                side_CreateBtn.setVisible(true);
                            });
                            slider.play();
                            progressIndicator.setVisible(false);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, (error) -> {
                    progressIndicator.setVisible(false);
                    error.printStackTrace();
                });
            }catch(Exception e){e.printStackTrace();}
        } else {
            General.getErrorAlert(errorMsg);
        }
    }

    //enter username and email to locate account to reset password
    @FXML
    public void proceedBtn(ActionEvent event) {
        progressIndicator.setVisible(true);
        //check if username and email textfield are full
        if (fp_username.getText().isEmpty() || fp_email.getText().isEmpty()
        || fp_question.getSelectionModel().getSelectedItem() == null || fp_answer.getText().isEmpty()) {

            General.getErrorAlert("Please fill all blank fields");
        }else{
            //if username and email textfield are both filled
            String username = this.fp_username.getText();
            String email = this.fp_email.getText();
            String answer = this.fp_answer.getText();

            userService.check_userAsync(username, email, answer, (value) -> {
                progressIndicator.setVisible(false);
                if (value) {
                    np_newPassForm.setVisible(true);// new password form visible
                    fp_questionForm.setVisible(false); //forgot password form removed
                }else{
                    //if user is not found
                    General.getErrorAlert("Incorrect Information");
                }
            }, (error) -> {
                progressIndicator.setVisible(false);
                error.printStackTrace();
            });
        }
    }


    //back button from question page
    @FXML
    public void backToLoginForm(ActionEvent event) {
        fp_questionForm.setVisible(false);
        si_loginForm.setVisible(true);

        fp_username.clear();
        fp_email.clear();
    }

    //change password form
    @FXML
    public void changePassBtn(){
        //if textfields are empty
        if (np_newPassword.getText().isEmpty() || np_confirmPassword.getText().isEmpty()) {
            General.getErrorAlert("Please fill all blank fields");
        }else{
            //check if both textfields have the same contents
            if (np_newPassword.getText().equals(np_confirmPassword.getText())) {

                String password = this.np_newPassword.getText();
                String username= this.fp_username.getText();

                //check if the length is less than 8
                if (password.length()<8){
                    General.getErrorAlert("Invalid Password, at least 8 characters are needed");

                }else{
                    //alert that password have been reset
                    if(userService.change_password(username, password)){
                        General.getInfoAlert("Successfully changed Password!");

                        si_loginForm.setVisible(true);
                        np_newPassForm.setVisible(false);

                        // TO CLEAR FIELDS
                        np_confirmPassword.setText("");
                        np_newPassword.setText("");
                        fp_email.setText("");
                        fp_username.setText("");

                    }else{
                        //password hasn't been reset
                        General.getErrorAlert("Passwords do not match");

                    }
                }
            }else{
                General.getErrorAlert("Passwords do not match");
            }
        }
    }

    //from new password form to question form
    @FXML
    public void backToQuestionForm(){
        np_newPassForm.setVisible(false);
        fp_questionForm.setVisible(true);

        changePassBtn();
    }

    //switch from sign in and sign up
    @FXML
    public void switchForm(ActionEvent event){
        TranslateTransition slider = new TranslateTransition();

        //check if clicked on already have
        if (event.getSource() == side_CreateBtn) {
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));

            //sideForm will slide to reveal log in page
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_CreateBtn.setVisible(false);

                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);

            });

            slider.play();

            //if Create account is clicked
        } else if (event.getSource() == side_alreadyHave) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));

            //reveal sign up form
            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_CreateBtn.setVisible(true);

                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);
            });
            slider.play();
        }
    }


}

