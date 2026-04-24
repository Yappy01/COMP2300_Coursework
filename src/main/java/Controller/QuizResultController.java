package Controller;

import Models.Quiz;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizResultController {

    @FXML private Label resultLabel;
    @FXML private Label usernameLbl;
    @FXML private ToggleGroup questions;


    @FXML public void initialize() {
        usernameLbl.setText(utils.Session.getInstance().getUserName());
        questions.getToggles().forEach(toggle -> {
            if (toggle instanceof Node node) {
                node.setDisable(true);
            }
        });
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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/homePage.fxml"));
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
