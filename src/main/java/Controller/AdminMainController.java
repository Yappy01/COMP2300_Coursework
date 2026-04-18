package Controller;

import Models.Post;
import Models.StiEntry;
import Models.User;
import Service.PostService;
import Service.StiService;
import Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
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

public class AdminMainController implements PostParent {

    @FXML private StackPane mainPageArea;
    @FXML private Button loadMoreButton;
    @FXML private TilePane cardTiles;
    @FXML private StackPane mainPostPage;
    @FXML private StackPane addPostPage;

    @FXML private ScrollPane postScrollPage;
    @FXML private TextField filterField;
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

    private ComPageOverlayController comPageOverlayController;

    private ObservableList<Object> originalMasterData = FXCollections.observableArrayList();
    private FilteredList<Object> filteredData = new FilteredList<>(originalMasterData, p -> true);
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
    private final PostService postService = new PostService();
    private final StiService stiService = new StiService();
    private ArrayList<Post> postsList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<StiEntry> stiList = new ArrayList<>();

    public void initialize() {
        postScrollPage.setVisible(false);
        commonTopBarController.setUp("Admin Page", Session.getInstance().getUserName());

        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);
        generateUserTable();
    }


    @FXML
    private void generateInfoPageTable() {
        postScrollPage.setVisible(false);
        progressIndicator.setVisible(true);
        TableView<StiEntry> stiTable = new TableView<>();

        if (adminPageTable != null) {
            stiTable.getStyleClass().addAll(adminPageTable.getStyleClass());
        }

        TableColumn<StiEntry, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("stiId"));

        TableColumn<StiEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StiEntry, String> symptomsColumn = new TableColumn<>("Symptoms");
        symptomsColumn.setCellValueFactory(new PropertyValueFactory<>("symptoms"));

        TableColumn<StiEntry, String> treatmentColumn = new TableColumn<>("Treatment");
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));

        TableColumn<StiEntry, String> preventionColumn = new TableColumn<>("Prevention");
        preventionColumn.setCellValueFactory(new PropertyValueFactory<>("prevention"));

        TableColumn<StiEntry, Integer> riskLevelColumn = new TableColumn<>("riskLevel");
        riskLevelColumn.setCellValueFactory(new PropertyValueFactory<>("riskLevel"));

        stiTable.getColumns().addAll(idColumn, nameColumn, symptomsColumn, treatmentColumn, preventionColumn, riskLevelColumn);
        loadTableData(StiEntry.class);

        adminPageTable = (TableView<Object>) (Object) stiTable;
        // Clear the VBox and show the NEW table
        tableViewArea.getChildren().set(0, stiTable);

        try {
            generateInputBoxes();
            loadTableData(StiEntry.class);

            deleteButton.setOnAction(e -> {
                StiEntry stiEntry = (StiEntry) idMenuSelection.getValue();
                stiService.deleteStiAsync(stiEntry.getStiId(), (value) -> {
                    progressIndicator.setVisible(false);
                    if (value) {
                        General.getInfoAlert("Sti Entry Deleted Successfully");
                    }
                    loadTableData(StiEntry.class);
                }, (error) -> {
                    progressIndicator.setVisible(false);
                    error.printStackTrace();
                });
            });

            editOrAddButton.setOnAction(e -> {
                deleteButton.setDisable(false);
                clearInputs();

                if (editOrAddButton.getText().equals("Edit Mode")) { //Currently is in edit mode changing to add mode
                    idMenuSelection.setDisable(true);

                    editOrAddButton.setText("Add Mode");
                    deleteButton.setDisable(true);
                } else { //Currently is in Add mode changing to edit mode
                    clearInputs();
                    Object selected = idMenuSelection.getValue();
                    if (selected != null) {
                        autoFillFields(selected);
                    }
                    idMenuSelection.setDisable(false);

                    editOrAddButton.setText("Edit Mode");
                    deleteButton.setDisable(false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        configConfirmButton(StiEntry.class);
    }

    @FXML
    private void generateUserTable() {
        postScrollPage.setVisible(false);
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

        adminPageTable = (TableView<Object>) (Object) userTable;
        // Clear the VBox and show the NEW table
        tableViewArea.getChildren().set(0, userTable);

        try {
            generateInputBoxes();
            loadTableData(User.class);

            deleteButton.setOnAction(e -> {
                User user = (User) idMenuSelection.getValue();
                userService.deleteUserAsync(user, (value) -> {
                    progressIndicator.setVisible(false);
                    if (value) {
                        General.getInfoAlert("User Deleted Successfully");
                    }
                    loadTableData(User.class);
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
                        if (selected != null) {
                            autoFillFields(selected);
                        }
                        idMenuSelection.setDisable(false);

                        Node ans = inputField.lookup("#Answer");
                        Node pwd = inputField.lookup("#Password");
                        if (ans != null) ans.setVisible(false);
                        if (pwd != null) pwd.setVisible(false);

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
        configConfirmButton(User.class);
    }

    public <T> void loadTableData(Class<T> type) {
        System.out.println("LOad data");
        if (type == User.class) {
            System.out.println("Loading user data");
            progressIndicator.setVisible(true);
            userService.getAllUserAsync((allUsers) -> {
                progressIndicator.setVisible(false);

                FilteredList<Object> filteredData = new FilteredList<>(
                        FXCollections.observableArrayList(allUsers), p -> true
                );

                filterField.textProperty().addListener((obs, oldVal, newVal) -> {
                    filteredData.setPredicate(item -> {
                        if (newVal == null || newVal.isBlank()) return true;
                        String filter = newVal.toLowerCase();
                        return getSearchString(item).contains(filter);
                    });
                });

                // Update UI Components
                adminPageTable.setItems(filteredData);
                idMenuSelection.setItems(filteredData);
                idMenuSelection.setOnAction(e -> {
                    Object selected = idMenuSelection.getValue();
                    if (selected != null) {
                        autoFillFields(selected);
                    }
                });
                clearInputs();
            }, (error) -> {
                progressIndicator.setVisible(false);
                error.printStackTrace();
            });
        } else if (type == StiEntry.class) {
            System.out.println("Loading Sti Data");
            progressIndicator.setVisible(true);
            stiService.getAllAsync((allStis) -> {
                progressIndicator.setVisible(false);

                FilteredList<Object> filteredData = new FilteredList<>(
                        FXCollections.observableArrayList(allStis), p -> true
                );

                filterField.textProperty().addListener((obs, oldVal, newVal) -> {
                    filteredData.setPredicate(item -> {
                        if (newVal == null || newVal.isBlank()) return true;
                        String filter = newVal.toLowerCase();
                        return getSearchString(item).contains(filter);
                    });
                });

                adminPageTable.setItems(filteredData);
                idMenuSelection.setItems(filteredData);
                idMenuSelection.setOnAction(e -> {
                    Object selected = idMenuSelection.getValue();
                    if (selected != null) {
                        autoFillFields(selected);
                    }
                });
                clearInputs();
            }, (error) -> {
                error.printStackTrace();
                progressIndicator.setVisible(false);
            });
        }
    }

    public <T> void configConfirmButton(Class<T> type) {
        confirmButton.setOnAction(e -> {
            if (type == User.class) {
                InputBoxController nameController = controllerMap.get("Name");
                InputBoxController emailController = controllerMap.get("Email");
                InputBoxController roleController = controllerMap.get("Role");

                if (editOrAddButton.getText().equals("Add Mode")) {
                    InputBoxController passwordController = controllerMap.get("Password");
                    InputBoxController answerController = controllerMap.get("Answer");

                    progressIndicator.setVisible(true);

                    String errorMsg = userService.validateCredentials(nameController.getInputText(), passwordController.getInputText(), emailController.getInputText(), answerController.getInputText(), "Example Question");
                    if (errorMsg.isEmpty()) {
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
                        loadTableData(User.class);
                    } else {
                        General.getErrorAlert(errorMsg);
                    }
                } else {
                    User user = (User) idMenuSelection.getValue();
                    progressIndicator.setVisible(true);
                    User editedUser = new User(user.getUserId(), nameController.getInputText(), emailController.getInputText(), roleController.getInputText());
                    userService.updateUserAsync(editedUser, (value) -> {
                        progressIndicator.setVisible(false);
                        if (value) {
                            General.getInfoAlert("User updated successfully");
                            loadTableData(User.class);
                        }
                    }, (error) -> {
                        progressIndicator.setVisible(false);
                        error.printStackTrace();
                    });
                }
            } else if (type == StiService.class) {
                if (editOrAddButton.getText().equals("Add Mode")) {
                    InputBoxController nameController = controllerMap.get("Name");
                    InputBoxController symptomsController = controllerMap.get("Symptoms");
                    InputBoxController treatmentController = controllerMap.get("Treatment");
                    InputBoxController preventionController = controllerMap.get("Prevention");
                    InputBoxController riskLevelController = controllerMap.get("Risk Level");

                    if (editOrAddButton.getText().equals("Add Mode")) {
                        progressIndicator.setVisible(true);

                        String errorMsg = stiService.validateInput(nameController.getInputText(), symptomsController.getInputText(), treatmentController.getInputText(), preventionController.getInputText(), riskLevelController.getInputText());
                        if (errorMsg.isEmpty()) {
                            StiEntry stiEntry = new StiEntry(nameController.getInputText(), symptomsController.getInputText(), treatmentController.getInputText(), preventionController.getInputText(), Integer.valueOf(riskLevelController.getInputText()));
                            stiService.insertStiInfo(stiEntry, (value) -> {
                                progressIndicator.setVisible(false);
                                if (value) {
                                    General.getInfoAlert("New Sti Entry Added Successfully");
                                }
                            }, (error) -> {
                                progressIndicator.setVisible(false);
                                error.printStackTrace();
                            });

                            loadTableData(StiEntry.class);
                        } else {
                            General.getErrorAlert(errorMsg);
                        }
                    } else {
                        StiEntry stiEntry = (StiEntry) idMenuSelection.getValue();
                        progressIndicator.setVisible(true);
                        StiEntry editedEntry = new StiEntry(stiEntry.getStiId(), nameController.getInputText(), symptomsController.getInputText(), treatmentController.getInputText(), preventionController.getInputText(), Integer.valueOf(riskLevelController.getInputText()));
                        stiService.updateStiInfo(editedEntry, (value) -> {
                            progressIndicator.setVisible(false);
                            if (value) {
                                General.getInfoAlert("User updated successfully");
                                loadTableData(User.class);
                            }
                        }, (error) -> {
                            progressIndicator.setVisible(false);
                            error.printStackTrace();
                        });
                        loadTableData(StiEntry.class);
                    }
                }
            }
        });
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

    @FXML
    private void loadAdminPostPage() {
        postScrollPage.setVisible(true);

        OverlayBController overlayBController = null;
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);

        try {
            FXMLLoader overlayLoader = new FXMLLoader(getClass().getResource("/fxml/components/comPostOverlay.fxml"));
            mainPostPage = overlayLoader.load();

            mainPostPage.setVisible(false);
            comPageOverlayController = overlayLoader.getController();
            comPageOverlayController.setParentController(this);
            mainPageArea.getChildren().add(mainPostPage);

            FXMLLoader overlayBLoader = new FXMLLoader(getClass().getResource("/fxml/components/comPostOverlay2.fxml"));
            addPostPage = overlayBLoader.load();
            addPostPage.setVisible(false);
            overlayBController = overlayBLoader.getController();
            overlayBController.setParentController(this);
            mainPageArea.getChildren().add(addPostPage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        reloadCards();
        postScrollPage.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                System.out.println("At the bottom!");
                loadMoreButton.setVisible(true);
            }
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

    private String getSearchString(Object obj) {
        if (obj == null) return "";
        StringBuilder sb = new StringBuilder();
        for (java.lang.reflect.Field field : obj.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object val = field.get(obj);
                if (val != null) sb.append(val.toString().toLowerCase()).append(" ");
            } catch (Exception e) { /* Skip inaccessible fields */ }
        }
        return sb.toString();
    }

    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }


    public void setOverlayVisibility(Boolean visibility) {
        mainPostPage.setVisible(visibility);
    }

    public void setOverlayBVisibility(Boolean visibility) {
        addPostPage.setVisible(visibility);
    }

    @Override
    public void setProgressIndicatorVisibility(Boolean value) {
        progressIndicator.setVisible(value);
    }

    @Override
    public StackPane getAddPostPage() {
        return addPostPage;
    }

    @Override
    public void reloadCards() {
        progressIndicator.setVisible(true);

        postService.getAllPostsAsync("likes", Session.getInstance().getLoadedPostNum(), true,
                (allPost) -> {
                    postsList.clear();
                    cardTiles.getChildren().clear();

                    postsList = allPost;
                    loadCards(); // UI update
                    progressIndicator.setVisible(false);
                },
                (error) -> {
                    error.printStackTrace();
                    progressIndicator.setVisible(false);
                });
    }

    @Override
    public void loadCards() {
        for (int i = 0; i < postsList.size(); i++) {
            try {
                final int index = i;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card.fxml"));
                Node card = loader.load();

                CardController controller = loader.getController();
                controller.setParentController(this);
                Post post = postsList.get(i);

                userService.getUserName(post.getUserId(), (name) -> {
                    controller.setComPageOverlayController(comPageOverlayController);
                    controller.setPost(postsList.get(index));
                    controller.setData(name, post.getContent(), post.getCreatedAt(), post.getLikeCount(), post.getCommentCount(), post.getImageLink());
                    cardTiles.getChildren().add(card);

                    if (index == postsList.size()) {
                        progressIndicator.setVisible(false);
                    }
                }, (error) -> {
                    error.printStackTrace();
                    if (index == postsList.size()) {
                        progressIndicator.setVisible(false);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoadMoreButtonClick(){
        if (postsList.size() == Session.getInstance().getLoadedPostNum()) {
            Session.getInstance().setLoadedPostNum(Session.getInstance().getLoadedPostNum() + 12);
            reloadCards();
        } else {
            General.getInfoAlert("No more Posts");
        }
    }
}
