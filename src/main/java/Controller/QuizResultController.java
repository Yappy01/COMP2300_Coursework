package Controller;

import Models.Quiz;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class QuizResultController {

    @FXML private Label resultLabel;
    @FXML private Label usernameLbl;

    private QuizQuestionController quizQuestionController;

    @FXML public void initialize() {
        usernameLbl.setText(utils.Session.getInstance().getUserName());
    };

    public void show_result(int score){
        resultLabel.setText(Integer.toString(score) + "/8");

    }

    @FXML private void gotoMainQuiz(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/quizMain.fxml"));
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML void goToHomepage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/homePage.fxml"));
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML void gotoQuestionPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/quizQuestion.fxml"));
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML void gotoUserProfile() throws IOException {
        Parent root = FXMLLoader.load(
                HomePageController.class.getResource("/fxml/pages/UserProfile.fxml")
        );

        Stage stage = (Stage) resultLabel.getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

}
