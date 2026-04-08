package Controller;

import Models.Post;
import Models.User;
import Service.PostService;
import Service.UserService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
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

    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    private Parent commonTopBar;
    @FXML
    private CommonTopBarController commonTopBarController;

    private List<Post> postsList = new ArrayList<>();
    private ComPageOverlayController comPageOverlayController;
    private final PostService postService = new PostService();
    private final UserService userService = new UserService();

    public StackPane getAddPostPage() {
        return addPostPage;
    }

    public void setLoadingSpinnerVisibility(Boolean value) {
        loadingSpinner.setVisible(value);
    }

    @FXML
    public void initialize() {
        commonTopBarController.setUp("Community Page", Session.getInstance().getUserName());

        OverlayBController overlayBController = null;
        loadingSpinner.setVisible(false);
        loadingSpinner.setProgress(-1);

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
        loadingSpinner.setVisible(true);

        postService.getAllPostsAsync("recent",
                (allPost) -> {
                    postsList.clear();
                    cardTiles.getChildren().clear();

                    postsList = allPost;
                    loadCards(); // UI update
                    loadingSpinner.setVisible(false);
                },
                (error) -> {
                    error.printStackTrace();
                    loadingSpinner.setVisible(false);
                });
    }

    @FXML
    public void filterLikeCount() {
        loadingSpinner.setVisible(true);

        postService.getAllPostsAsync("likes",
                (allPost) -> {
                    postsList.clear();
                    cardTiles.getChildren().clear();

                    postsList = allPost;
                    loadCards(); // UI update
                    loadingSpinner.setVisible(false);
                },
                (error) -> {
                    error.printStackTrace();
                    loadingSpinner.setVisible(false);
                });
    }

    @FXML
    public void filterCommentCount() {
        loadingSpinner.setVisible(true);

        postService.getAllPostsAsync("comments",
                (allPost) -> {
                    postsList.clear();
                    cardTiles.getChildren().clear();

                    postsList = allPost;
                    loadCards(); // UI update
//                    loadingSpinner.setVisible(false);
                },
                (error) -> {
                    error.printStackTrace();
                    loadingSpinner.setVisible(false);
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

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                String tsString = sdf.format(post.getCreatedAt());

                userService.getUserName(post.getUserId(), (name) -> {
                    controller.setComPageOverlayController(comPageOverlayController);
                    controller.setPost(postsList.get(index));
                    controller.setData(name, post.getContent(), tsString, post.getLikeCount(), post.getCommentCount(), post.getImageLink());
                    cardTiles.getChildren().add(card);

                    if (index == postsList.size()) {
                        loadingSpinner.setVisible(false);
                    }
                }, (error) -> {
                    error.printStackTrace();
                    if (index == postsList.size()) {
                        loadingSpinner.setVisible(false);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void goToHomepage(ActionEvent event) throws IOException {
        HomePageController.goToHomepage(event);
    }
}
