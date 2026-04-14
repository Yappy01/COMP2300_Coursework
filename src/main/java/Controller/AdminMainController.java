package Controller;

import Models.User;
import Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import utils.Session;

import java.io.IOException;
import java.util.ArrayList;

public class AdminMainController {
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Parent commonTopBar;
    @FXML private CommonTopBarController commonTopBarController;

    private final UserService userService = new UserService();
    private ArrayList<User> userList = new ArrayList<>();

    public void initialize() {
        commonTopBarController.setUp("Information Page", Session.getInstance().getUserName());

        progressIndicator.setVisible(true);
        progressIndicator.setProgress(-1);
    }

    public void generateUserTable() {
        TableView<User> userTable = new TableView<>();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTable.getColumns().addAll(idCol, nameCol, emailCol);
        userTable.getStylesheets().add(getClass().getResource("/css/infotableView.css").toExternalForm());

        userService.getAllUserAsync((allUsers) -> {
            progressIndicator.setVisible(false);
            userList = allUsers;

            ObservableList<User> masterData = FXCollections.observableArrayList(userList);
            userTable.setItems(masterData);
            for (TableColumn<?, ?> col : userTable.getColumns()) {
                col.setResizable(false);
                col.setReorderable(false);
                col.setSortable(false);
            }
        }, (error) -> {
            progressIndicator.setVisible(false);
            error.printStackTrace();
        });
    }

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
