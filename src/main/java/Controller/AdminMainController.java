package Controller;

import Models.User;
import Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import utils.General;
import utils.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminMainController {

    @FXML private Button confirmButton;
    @FXML private Button editOrAddButton;
    @FXML private Button deleteButton;
    @FXML private ComboBox<Object> idMenuSelection;
    @FXML private VBox inputField;
    @FXML private HBox tableViewArea;
    @FXML private TableView<Object> adminPageTable;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Parent commonTopBar;
    @FXML private CommonTopBarController commonTopBarController;

    private Map<String, InputBoxController> controllerMap = new HashMap<>();
    private final StringConverter<Object> stringConverter = new StringConverter<Object>() {
        @Override
        public String toString(Object object) {
            if (object == null) {
                return "";
            }
            // Cast Object to User to access getUserId()
            if (object instanceof User) {
                User user = (User) object;
                return String.valueOf(user.getUserId());
            }
            return object.toString();
        }
        @Override
        public User fromString(String string) {
            // This is rarely used unless the combo box is editable
            return null;
        }
    };
    private final UserService userService = new UserService();
    private ArrayList<User> userList = new ArrayList<>();

    public void initialize() {
        commonTopBarController.setUp("Admin Page", Session.getInstance().getUserName());

        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);
        generateUserTable();

        confirmButton.setOnAction(e -> { //Currently only configured for User Table
            InputBoxController nameController = controllerMap.get("Name");
            InputBoxController emailController = controllerMap.get("Email");
            InputBoxController roleController = controllerMap.get("Role");

            if (editOrAddButton.getText().equals("Add Mode")) {
                InputBoxController passwordController = controllerMap.get("Password");
                InputBoxController answerController = controllerMap.get("Answer");

                confirmButton.setOnAction(event -> {
                    progressIndicator.setVisible(true);
                    User user = new User(nameController.getInputText(), passwordController.getInputText(), emailController.getInputText(), answerController.getInputText(), roleController.getInputText());
                    userService.register_userAsync(user, (value) -> {
                        progressIndicator.setVisible(false);
                        if (value) {
                            General.getInfoAlert("New User Added Successfully");
                        }
                    }, (error) -> {
                        progressIndicator.setVisible(false);
                        error.printStackTrace();
                    });

                    reloadTableData(User.class);
                });
            } else {
                confirmButton.setOnAction(event -> {
                    System.out.println("EDITINGGGGGG");
                    User user = (User) idMenuSelection.getValue();
                    progressIndicator.setVisible(true);
                    User editedUser = new User(user.getUserId(), nameController.getInputText(), emailController.getInputText(), roleController.getInputText());
                    userService.updateUserAsync(editedUser, (value) -> {
                        progressIndicator.setVisible(false);
                        if (value) {
                            General.getInfoAlert("User updated successfully");
                            reloadTableData(User.class);
                        }
                    }, (error) -> {
                        progressIndicator.setVisible(false);
                        error.printStackTrace();
                    });
                });
            }
        });
    }

    public void generateUserTable() {
        progressIndicator.setVisible(true);
        TableView<User> userTable = new TableView<>();

        if (adminPageTable != null) {
            userTable.getStyleClass().addAll(adminPageTable.getStyleClass());
        }

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        userTable.getColumns().addAll(idCol, nameCol, emailCol, roleCol);

        userService.getAllUserAsync((allUsers) -> {
            progressIndicator.setVisible(false);
            userList = allUsers;

            ObservableList<User> masterData = FXCollections.observableArrayList(userList);
            userTable.setItems(masterData);

            idMenuSelection.setItems(adminPageTable.getItems());
            idMenuSelection.setConverter(stringConverter);
            idMenuSelection.setOnAction(event -> {
                Object selected = idMenuSelection.getValue();
                autoFillFields(selected);
            });
        }, (error) -> {
            progressIndicator.setVisible(false);
            error.printStackTrace();
        });

        adminPageTable = (TableView<Object>) (Object) userTable;
        // Clear the VBox and show the NEW table
        tableViewArea.getChildren().set(0, userTable);

        try {
            generateInputBoxes();

            deleteButton.setOnAction(e -> {
                User user = (User) idMenuSelection.getValue();
                userService.deleteUserAsync(user, (value) -> {
                    progressIndicator.setVisible(false);
                    if (value) {
                        General.getInfoAlert("User Deleted Successfully");
                    }
                    reloadTableData(User.class);
                }, (error) -> {
                    progressIndicator.setVisible(false);
                    error.printStackTrace();
                });
            });

            editOrAddButton.setOnAction(e -> {
                try {
                    deleteButton.setDisable(false);
                    clearInputs();

                    if (editOrAddButton.getText().equals("Edit Mode")) { //Currently is in edit mode changing to add mode
                        idMenuSelection.setDisable(true);
                        if (inputField.lookup("#Password") == null) {
                            //Individually adding new input boxes for rows not shown in the table
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/inputBoxes.fxml"));
                            Node inputBox = loader.load();

                            InputBoxController controller = loader.getController();
                            controller.configInputBox("Password", "");
                            inputBox.setId("Password");
                            inputField.getChildren().add(inputBox);
                            controllerMap.put("Password", controller);

                            //Individually adding new input boxes for rows not shown in the table
                            loader = new FXMLLoader(getClass().getResource("/fxml/components/inputBoxes.fxml"));
                            inputBox = loader.load();

                            controller = loader.getController();
                            controller.configInputBox("Answer", "");
                            inputBox.setId("Answer");
                            inputField.getChildren().add(inputBox);
                            controllerMap.put("Answer", controller);
                        } else {
                            inputField.lookup("#Answer").setVisible(true);
                            inputField.lookup("#Password").setVisible(true);
                        }

                        editOrAddButton.setText("Add Mode");
                        deleteButton.setDisable(true);
                    } else { //Currently is in Add mode changing to edit mode
                        clearInputs();
                        Object selected = idMenuSelection.getValue();
                        autoFillFields(selected);
                        idMenuSelection.setDisable(false);
                        inputField.lookup("#Answer").setVisible(false);
                        inputField.lookup("#Password").setVisible(false);

                        editOrAddButton.setText("Edit Mode");
                        deleteButton.setDisable(false);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> void reloadTableData(Class<T> type) {
        if  (type == User.class) {
            clearInputs();
            generateUserTable();
        } else {
            System.out.println("NOT RELOAIDNG");
        }
    }

    public void generateInputBoxes() throws IOException {
        int columns = inputField.getChildren().size();
        inputField.getChildren().remove(2, columns);
        for (TableColumn<Object, ?> col : adminPageTable.getColumns()) {
            if (col.getText().equals("ID")) {
                continue;
            }
            col.setPrefWidth(120);
            col.setMinWidth(120);
            col.setMaxWidth(120);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/inputBoxes.fxml"));
            Node inputBox = loader.load();

            InputBoxController controller = loader.getController();
            controller.configInputBox(col.getText(), "");
            controllerMap.put(col.getText(), controller);
            inputBox.setId(col.getText()); // Check if actually needed
            inputField.getChildren().add(inputBox);
        }
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

    private void autoFillFields(Object object) {
        if (object == null) return;

        try {
            for (TableColumn<Object, ?> col : adminPageTable.getColumns()) {
                String columnName = col.getText();

                Object value = col.getCellData(object);
                String stringValue = (value == null) ? "" : value.toString();

                InputBoxController controller = controllerMap.get(columnName);

                if (controller != null) {
                    controller.configInputBox(columnName, stringValue);
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void clearInputs() {
        try {
            for (TableColumn<Object, ?> col : adminPageTable.getColumns()) {
                String columnName = col.getText();

                InputBoxController controller = controllerMap.get(columnName);

                if (controller != null) {
                    controller.configInputBox(columnName, "");
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }
}
