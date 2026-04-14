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
import java.util.*;

public class QuizQuestionController {
    @FXML private void handleSearch(){
    }

    @FXML private ToggleGroup questions; // Maps to fx:id="questions" in FXML [cite: 22]
    @FXML private ToggleButton resultTBtn;   // Maps to fx:id="result"

    // UI Elements for the question and 4 choices
    @FXML private Label questionLabel;
    @FXML private Label questionNumberLabel;
    @FXML private ToggleButton option1TBtn, option2TBtn, option3TBtn, option4TBtn;
    @FXML private ToggleGroup options;

    private List<Quiz> activeQuizzes = new ArrayList<>();
    private final Map<Integer, String> userAnswers = new HashMap<>();
    private int currentQuestionIndex = 0;
    private final QuizService quizService = new QuizService();
    private int quizCategory;

//    public void initialize() {
//        // Example: Loading category 1 (STI Quiz)
//        loadQuiz();
//    }

    public void loadQuizCategory(int quizCategory) {
        this.quizCategory = quizCategory;
    }



    // Update setQuizzes to actually show the first question once data is received
    public void setQuizzes(List<Quiz> quizzes) {
        this.activeQuizzes = new ArrayList<>(quizzes);
        if (!activeQuizzes.isEmpty()) {
            Collections.shuffle(activeQuizzes);
            this.activeQuizzes = activeQuizzes.subList(0, Math.min(8, activeQuizzes.size()));
            showQuestion(0); // Display the first question passed from the previous screen
        }
    }

    public void loadQuiz() {
        System.out.println("Loading Quiz");
        System.out.println("quizCategory: " + quizCategory);

        quizService.getQuizzesAsync(quizCategory, allQuizzes -> {
            // Shuffle and pick exactly 8 questions
            Collections.shuffle(allQuizzes);
            this.activeQuizzes = allQuizzes.subList(0, Math.min(8, allQuizzes.size()));
            showQuestion(0);
        }, error -> error.printStackTrace());
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        ToggleButton selectedBtn = (ToggleButton) event.getSource();

        if (selectedBtn == resultTBtn) {
            if (userAnswers.size() < 8) {
                // Warning: Not all questions answered
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please answer all 8 questions first!");
                alert.show();
                questions.selectToggle(questions.getToggles().get(currentQuestionIndex));
            } else {
                calculateResults();
            }
            return;
        }

        // Logic for Question buttons 1-8 [cite: 20-39]
        String btnText = selectedBtn.getText(); // e.g., "Question 1"
        currentQuestionIndex = Integer.parseInt(btnText.replaceAll("[^0-9]", "")) - 1;
        showQuestion(currentQuestionIndex);
    }

    private void showQuestion(int index) {
        System.out.println(activeQuizzes.size());
        Quiz q = activeQuizzes.get(index);
        if (questionNumberLabel != null) questionNumberLabel.setText("Question " + (index+1));

        //update the question
        questionLabel.setText(q.getQuestion());

        //update the options button
        option1TBtn.setText(q.getAnswer1());
        option2TBtn.setText(q.getAnswer2());
        option3TBtn.setText(q.getAnswer3());
        option4TBtn.setText(q.getAnswer4());

        // Restore previous answer if it exists
        options.selectToggle(null);
        if (userAnswers.containsKey(index)) {
            String saved = userAnswers.get(index);
            for (Toggle t : options.getToggles()) {
                if (((ToggleButton)t).getText().equals(saved)) t.setSelected(true);
            }
        }
    }

    @FXML
    private void next_question(ActionEvent event) { //automating advance to next question
        ToggleButton selected = (ToggleButton) options.getSelectedToggle();
        if (selected != null) {
            // Save answer
            userAnswers.put(currentQuestionIndex, selected.getText());

            if (currentQuestionIndex < 7) {
                currentQuestionIndex++;
                questions.selectToggle(questions.getToggles().get(currentQuestionIndex));
                showQuestion(currentQuestionIndex);
            }
        }
    }


    private void calculateResults() {
        int score = 0;
        for (int i = 0; i < activeQuizzes.size(); i++) {
            if (activeQuizzes.get(i).getCorrectAnswer().equals(userAnswers.get(i))) {
                score++;
            }
        }
        // Logic to switch to result view and display "score/8" [cite: 81]
    }

    @FXML void goToHomepage(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(
//                HomePageController.class.getResource("/fxml/pages/homePage.fxml")
//        );
//
//        Stage stage = (Stage) questionLabel.getScene().getWindow();
//
//        stage.setScene(new Scene(root));
//        stage.show();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/homePage.fxml"));
        Stage stage = (Stage) questionLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }
}
