package Controller;

import Models.Post;
import Service.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import utils.General;
import utils.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YourPostController implements PostParent{
    @FXML
    private TilePane cardTiles;
    @FXML
    private StackPane comPageArea;
    @FXML
    private StackPane mainPostPage;
    @FXML
    private StackPane addPostPage;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ScrollPane postScrollPage;
    @FXML
    private Button loadMoreButton;
    @FXML
    private Parent commonTopBar;
    @FXML
    private CommonTopBarController commonTopBarController;
    private ComPageOverlayController comPageOverlayController;
    private OverlayBController overlayBController;
    private List<Post> postsList = new ArrayList<>();
    private final PostService postService = new PostService();
    private final CommunityPageController communityPageController = new CommunityPageController();

    public void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }

    public void onAddButtonClick() {
        setOverlayBVisibility(true);
    }

    @FXML
    public void onLoadMoreButtonClick(){
        if (postsList.size() == Session.getInstance().getLoadedPostNum()) {
            Session.getInstance().setLoadedPostNum(Session.getInstance().getLoadedPostNum() + 12);
            reloadCards();
        } else {
            General.getInfoAlert("No more Posts");
        }
    }

    public void setProgressIndicatorVisibility(Boolean value) {
        progressIndicator.setVisible(value);
    }

    public StackPane getAddPostPage() {
        return addPostPage;
    }

    public void reloadCards() {
        getPosts();
    }

    public void getPosts() {
        progressIndicator.setVisible(true);

        postService.getPostByUserAsync(Session.getInstance().getUserID(), Session.getInstance().getLoadedPostNum(),
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

    @FXML
    public static void goToYourPost(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                HomePageController.class.getResource("/fxml/pages/yourPost.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

    public void goToPIPage(ActionEvent event) throws IOException {
        UserProfileController.gotoPIPage(event);
    }

    public void initialize(){
        commonTopBarController.setUp("Your Post Page", Session.getInstance().getUserName());
        loadMoreButton.setVisible(false);
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);

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

        reloadCards();

        postScrollPage.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                System.out.println("At the bottom!");
                loadMoreButton.setVisible(true);
            }
            else {
                loadMoreButton.setVisible(false);
            }
        });
    }

    public void loadCards() {
        for (int i = 0; i < postsList.size(); i++) {
            try {
                final int index = i;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card.fxml"));
                Node card = loader.load();

                CardController controller = loader.getController();
                controller.setParentController(this);
                Post post = postsList.get(i);

                controller.setComPageOverlayController(comPageOverlayController);
                controller.setOverlayBController(overlayBController);
                controller.setPost(postsList.get(index));
                controller.setData(Session.getInstance().getUserName(), post.getContent(), post.getCreatedAt(), post.getLikeCount(), post.getCommentCount(), post.getImageLink());
                cardTiles.getChildren().add(card);

                if (index == postsList.size()) {
                    progressIndicator.setVisible(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOverlayVisibility(Boolean visibility) {
        mainPostPage.setVisible(visibility);
    }

    public void setOverlayBVisibility(Boolean visibility) {
        addPostPage.setVisible(visibility);
    }
}
