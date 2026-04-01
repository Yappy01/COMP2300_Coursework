package Controller;

import Models.Post;
import Models.StiEntry;
import Service.PostService;
import Service.StiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.Session;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class InformationPageController {
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ToggleButton riskLevelButton;
    @FXML private ToggleButton nameButton;
    @FXML private ToggleButton symptomsButton;
    @FXML private Button searchButton;
    private String searchMode = "symptoms";
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
    @FXML
    private HBox commonTopBar;
    private CommonTopBarController commonTopBarController;
    private ObservableList<StiEntry> masterData;
    private ArrayList<StiEntry> stis;
    private final StiService stiService = new StiService();

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/commonTopBar.fxml"));
            loader.load();
            commonTopBarController = (CommonTopBarController) loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        commonTopBarController.setUp("Information Page", Session.getInstance().getUserName());

        progressIndicator.setVisible(true);
        progressIndicator.setProgress(-1);
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
        stiService.getAllAsync((allStis) -> {
            progressIndicator.setVisible(false);
            stis = allStis;
            masterData = FXCollections.observableArrayList(stis);
            stiContentTable.setItems(masterData);
            stiContentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            searchButton.setOnAction(e1 -> {
                switch (searchMode) {
                    case "name" -> searchName();
                    case "risk" -> searchRiskLevel();
                    case "symptoms" -> searchSymptoms();
                }
            });
        }, (error) -> {
            error.printStackTrace();
            progressIndicator.setVisible(false);
        });
    }

    @FXML
    private void searchName() {
        progressIndicator.setVisible(true);
        String filterText = filterField.getText().toLowerCase();

        stiService.searchByNameAsync(masterData, filterText,
            (result) -> {
                stiContentTable.setItems(FXCollections.observableArrayList(result));
                progressIndicator.setVisible(false);
            },
            (error) -> {
                error.printStackTrace();
                progressIndicator.setVisible(true);
            });
    }

    @FXML
    private void searchSymptoms() {
        progressIndicator.setVisible(true);
        String filterText = filterField.getText().toLowerCase();

        stiService.searchBySymptomsAsync(masterData, filterText,
                (result) -> {
                    stiContentTable.setItems(FXCollections.observableArrayList(result));
                    progressIndicator.setVisible(false);
                },
                (error) -> {
                    error.printStackTrace();
                    progressIndicator.setVisible(true);
                });
    }

    @FXML
    private void searchRiskLevel() {
        progressIndicator.setVisible(true);
        String filterText = filterField.getText().toLowerCase();

        stiService.searchByRiskLevelAsync(masterData, filterText,
                (result) -> {
                    stiContentTable.setItems(FXCollections.observableArrayList(result));
                    progressIndicator.setVisible(false);
                },
                (error) -> {
                    error.printStackTrace();
                    progressIndicator.setVisible(true);
                });
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
        HomePageController.goToHomepage(event);
    }
}
