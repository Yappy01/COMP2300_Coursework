package Controller;

import DBHandling.StiDatabase;
import Models.StiEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class InformationPageController {
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
//    chlamydiaButton
}
