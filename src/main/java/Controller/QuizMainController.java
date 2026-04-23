package Controller;

import Models.Quiz;
import Service.QuizService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class QuizMainController {

    @FXML private MenuButton quiz_title;
    @FXML private Label usernameLbl;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button startButton;
    private int quizCategory;

    private final QuizService quizService = new QuizService();
    private List<Quiz> currentQuizList;



    @FXML public void initialize() {
        usernameLbl.setText(utils.Session.getInstance().getUserName());
        progressIndicator.setVisible(false);
    }

    @FXML
    public void handleQuizSelection(ActionEvent event) throws IOException {
        progressIndicator.setVisible(true);
        startButton.setDisable(true);
        Object source = event.getSource();

        if (source instanceof MenuItem) {
            MenuItem item = (MenuItem) source;
            String selectedText = item.getText();

            quiz_title.setText(selectedText);

            // Map the text to your database type IDs
           final int typeId = selectedText.equals("STI quiz") ? 1 : 2;
           quizCategory = typeId;
//           return typeId;
            // Async call to fetch data
            quizService.getQuizzesAsync(typeId,
                    quizzes -> {
                        this.currentQuizList = quizzes;
                        quiz_title.setText(selectedText);
                        progressIndicator.setVisible(false);
                        startButton.setDisable(false);
                    },
                    error -> {
                        error.printStackTrace();
                        progressIndicator.setVisible(false);
                        utils.General.getErrorAlert("Quiz was not loaded");
                    });

        } else {
            System.out.println("Source was not a MenuItem: " + source.getClass());
        }
    }



    @FXML void startQuiz(ActionEvent event) throws IOException {
        // Check if the list is null or empty before proceeding
        if (currentQuizList == null || currentQuizList.isEmpty()) {
            utils.General.getErrorAlert("Please select a quiz category and wait for it to load!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pages/quizQuestion.fxml"));
            Parent root = loader.load();
            QuizQuestionController targetController = loader.getController();

            targetController.loadQuizCategory(quizCategory);
            targetController.setQuizzes(currentQuizList);

            Stage stage = (Stage) (startButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Could not load quizQuestion.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


   @FXML void goToHomepage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                HomePageController.class.getResource("/fxml/pages/homePage.fxml")
        );

       Stage stage = (Stage) startButton.getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML void gotoUserProfile() throws IOException {
        Parent root = FXMLLoader.load(
                HomePageController.class.getResource("/fxml/pages/UserProfile.fxml")
        );

        Stage stage = (Stage) startButton.getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }
}