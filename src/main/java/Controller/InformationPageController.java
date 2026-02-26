package Controller;

import DBHandling.StiDatabase;
import Models.StiEntry;
import Service.StiService;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InformationPageController {
    @FXML private ToggleButton riskLevelButton;
    @FXML private ToggleButton nameButton;
    @FXML private ToggleButton symptomsButton;
    @FXML private Button searchButton;
    private String searchMode = "symptoms";
    private final StiService searchService = new StiService();
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

        // Wraps text in cells
        stiNameColumn.setCellFactory(wrapTextCellFactory());
        stiSymptomsColumn.setCellFactory(wrapTextCellFactory());
        stiTestAndCureColumn.setCellFactory(wrapTextCellFactory());
        stiTransmissionModeColumn.setCellFactory(wrapTextCellFactory());

        // Load data from DB
        ArrayList<StiEntry> stis = StiDatabase.getAll();
        masterData = FXCollections.observableArrayList(stis);
        stiContentTable.setItems(masterData);

        stiContentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        searchButton.setOnAction(e -> {
            switch (searchMode) {
                case "name" -> searchName();
                case "risk" -> searchRiskLevel();
                case "symptoms" -> searchSymptoms();
            }
        });
    }

    @FXML
    private void searchName() {
        String filterText = filterField.getText().toLowerCase();

        List<StiEntry> result =
                searchService.searchByName(masterData, filterText);

        stiContentTable.setItems(FXCollections.observableArrayList(result));
    }

    @FXML
    private void searchSymptoms() {
        String filterText = filterField.getText().toLowerCase();

        List<StiEntry> result =
                searchService.searchBySymptoms(masterData, filterText);

        stiContentTable.setItems(FXCollections.observableArrayList(result));
    }

    @FXML
    private void searchRiskLevel() {
        String filterText = filterField.getText().toLowerCase();

        List<StiEntry> result =
                searchService.searchByRiskLevel(masterData, filterText);

        stiContentTable.setItems(FXCollections.observableArrayList(result));
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        ToggleButton clicked = (ToggleButton) event.getSource();

        if (clicked == riskLevelButton) {
            searchMode = "risk";
            filterField.setPromptText("Search for risk level here.");

        } else if (clicked == nameButton) {
            searchMode = "name";
            filterField.setPromptText("Search for sti name here.");
        } else if (clicked == symptomsButton) {
            searchMode = "symptoms";
            filterField.setPromptText("Search for symptoms here.");
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

    // Makes Text in table column rapped
    private <T> Callback<TableColumn<T, String>, TableCell<T, String>> wrapTextCellFactory() {
        return tc -> new TableCell<>() {
            private final Text text = new Text();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(10));
                    setGraphic(text);
                }
            }
        };
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
