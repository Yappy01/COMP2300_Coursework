package Controller;

import DBHandling.StiDatabase;
import Models.StiEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class InformationPageController {
    @FXML private ToggleButton riskLevelButton;
    @FXML private ToggleButton nameButton;
    @FXML private ToggleButton symptomsButton;
    @FXML private Button searchButton;
    private String searchMode;
    @FXML
    private TableView<StiEntry> stiContentTable;
    @FXML
    private TableColumn<StiEntry, String> stiNameColumn;
    @FXML
    private TableColumn<StiEntry, String> stiSymptomsColumn;
    @FXML
    private TableColumn<StiEntry, String> stiTestAndCureColumn;
    @FXML
    private TableColumn<StiEntry, String> stiTransmissionModeColumn;
    @FXML
    private TableColumn<StiEntry, Integer> stiRiskLevelColumn;
    @FXML
    private TextField filterField;
    private ObservableList<StiEntry> masterData;

    @FXML
    public void initialize() {
        // Bind columns to model properties
        stiNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stiSymptomsColumn.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        stiTestAndCureColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        stiTransmissionModeColumn.setCellValueFactory(new PropertyValueFactory<>("prevention"));
        stiRiskLevelColumn.setCellValueFactory(new PropertyValueFactory<>("riskLevel"));

        // Load data from DB
        ArrayList<StiEntry> stis = StiDatabase.getAll();
        masterData = FXCollections.observableArrayList(stis);
        stiContentTable.setItems(masterData);
    }

    @FXML
    private void searchName() {
        String filterText = filterField.getText().toLowerCase();

        if (filterText.isEmpty()) {
            stiContentTable.setItems(masterData);
            return;
        }

        ObservableList<StiEntry> filteredData = masterData.filtered(
                sti -> sti.getName().toLowerCase().contains(filterText)
        );

        stiContentTable.setItems(filteredData);
    }

    @FXML
    private void searchSymptoms() {
        String filterText = filterField.getText().toLowerCase();

        if (filterText.isEmpty()) {
            stiContentTable.setItems(masterData);
            return;
        }

        ObservableList<StiEntry> filteredData = masterData.filtered(
                sti -> sti.getSymptoms().toLowerCase().contains(filterText)
        );

        stiContentTable.setItems(filteredData);
    }

    @FXML
    private void searchRiskLevel() {
        String filterText = filterField.getText().toLowerCase();

        if (filterText.isEmpty()) {
            stiContentTable.setItems(masterData);
            return;
        }
        ObservableList<StiEntry> filteredData;
        try {
            int risk = Integer.parseInt(filterText);

            filteredData = masterData.filtered(
                    sti -> sti.getRiskLevel() == risk
            );

            stiContentTable.setItems(filteredData);

        } catch (NumberFormatException e) {
            // If input invalid, just show all data
            stiContentTable.setPlaceholder(new Label("No data found"));
        }

    }

    @FXML
    private void handleSearch(ActionEvent event) {
        ToggleButton clicked = (ToggleButton) event.getSource();

        if (clicked == riskLevelButton) {
            clicked.setOnAction(e -> {
                searchMode = "risk";
                filterField.setPromptText("Search for risk level here.");
            });
        } else if (clicked == nameButton) {
            clicked.setOnAction(e -> {
                searchMode = "name";
                filterField.setPromptText("Search for sti name here.");
            });
        } else if (clicked == symptomsButton) {
            clicked.setOnAction(e -> {
                searchMode = "symptoms";
                filterField.setPromptText("Search for symptoms here.");
            });
        }

        searchButton.setOnAction(
                e -> {
                    if (searchMode.equals("name")) {
                        searchName();
                    } else if (searchMode.equals("risk")) {
                        searchRiskLevel();
                    } else if (searchMode.equals("symptoms")) {
                        searchSymptoms();
                    }
                }
        );
    }

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


}
