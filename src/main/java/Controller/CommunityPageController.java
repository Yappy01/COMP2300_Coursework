package Controller;

import DBHandling.ComPostDatabase;
import DBHandling.UserRepository;
import Models.Post;
import Models.User;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommunityPageController {
    @FXML
    private TilePane cardTiles;

    @FXML
    private StackPane comPageArea;

    @FXML
    private StackPane mainPostPage;

    @FXML
    private StackPane addPostPage;

    private List<Post> postsList = new ArrayList<>();
    private ComPageOverlayController comPageOverlayController;

    @FXML
    public void initialize() {
        OverlayBController overlayBController = null;

        //Remember to remove this after
        if (!Session.isLoggedIn()){
            UserService service = new UserService();
            User user = service.searchByUsername("Yappy");
            Session.startSession(user);
        }
        //This is just testing example user session

        try {
            FXMLLoader overlayLoader = new FXMLLoader(getClass().getResource("/fxml/components/comPostOverlay.fxml"));
            mainPostPage = overlayLoader.load();

            mainPostPage.setVisible(false);
            comPageOverlayController = overlayLoader.getController();
            comPageOverlayController.setParentController(this);
            comPageArea.getChildren().add(mainPostPage);

            FXMLLoader overlayBLoader = new FXMLLoader(getClass().getResource("/fxml/components/comPostOverlay2.fxml"));
            addPostPage = overlayBLoader.load();
            addPostPage.setVisible(false);
            overlayBController = overlayBLoader.getController();
            overlayBController.setParentController(this);
            comPageArea.getChildren().add(addPostPage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        filterRecent();
    }

    public void setOverlayVisibility(Boolean visibility) {
        mainPostPage.setVisible(visibility);
    }

    public void setOverlayBVisibility(Boolean visibility) {
        addPostPage.setVisible(visibility);
    }

    @FXML
    public void onAddButtonClick(){
        setOverlayBVisibility(true);
    }

    @FXML
    public void filterRecent() {
        System.out.println("recent");
        this.postsList.clear();
        this.cardTiles.getChildren().clear();
        System.out.println("cleared");
        this.postsList = Session.getInstance().getAllPosts("recent");
        System.out.println(this.postsList.size());
        loadCards();
    }

    @FXML
    public void filterLikeCount() {
        System.out.println("Likes");
        this.postsList.clear();
        this.cardTiles.getChildren().clear();
        System.out.println("cleared");
        this.postsList = Session.getInstance().getAllPosts("likes");
        System.out.println(this.postsList.size());
        loadCards();
    }

    @FXML
    public void filterCommentCount() {
        System.out.println("Comments");
        this.postsList.clear();
        this.cardTiles.getChildren().clear();
        System.out.println("cleared");
        this.postsList = Session.getInstance().getAllPosts("comments");
        System.out.println(this.postsList.size());
        loadCards();
    }

    public void loadCards() {
        for (int i = 0; i < postsList.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card.fxml"));
                Node card = loader.load();

                CardController controller = loader.getController();
                controller.setParentController(this);
                Post post = postsList.get(i);

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                String tsString = sdf.format(post.getCreatedAt());

                String name = UserRepository.getUserName(post.getUserId());
                controller.setComPageOverlayController(comPageOverlayController);
                controller.setPost(postsList.get(i));
                controller.setData(name, post.getContent(), tsString);

                cardTiles.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void goToHomepage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/pages/homePage.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }


}
