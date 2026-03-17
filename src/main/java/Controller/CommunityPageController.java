package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;

public class CommunityPageController {
    @FXML
    private TilePane cardTiles;

    @FXML
    private StackPane comPageArea;

    @FXML
    private StackPane mainPostPage;

    @FXML
    public void initialize() {
        try {
            FXMLLoader overlayLoader = new FXMLLoader(getClass().getResource("/fxml/components/comPostOverlay.fxml"));
            mainPostPage = overlayLoader.load();

            mainPostPage.setVisible(false);
            ComPageOverlayController comPageOverlayController = overlayLoader.getController();
            comPageOverlayController.setParentController(this);
            comPageArea.getChildren().add(mainPostPage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (int i = 1; i <= 12; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card.fxml"));
                Node card = loader.load();

                CardController controller = loader.getController();
                controller.setParentController(this);
                controller.setData("User 1","Content 1", "20/03/2026");

                cardTiles.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOverlayVisibility(Boolean visibility) {
        mainPostPage.setVisible(visibility);
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
