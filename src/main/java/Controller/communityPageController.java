package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

import java.io.IOException;

public class communityPageController {
    @FXML
    private TilePane cardTiles;

    @FXML
    public void initialize() {
        for (int i = 1; i <= 12; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/card.fxml"));
                Parent card = loader.load();

                CardController controller = loader.getController();
                controller.setData("User 1","Content 1", "20/03/2026");

                cardTiles.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
