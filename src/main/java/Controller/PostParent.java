package Controller;

import javafx.scene.layout.StackPane;

public interface PostParent {
    void setOverlayVisibility(Boolean visibility);
    void setOverlayBVisibility(Boolean visibility);
    void setProgressIndicatorVisibility(Boolean visibility);
    StackPane getAddPostPage();
    void reloadCards();
    void loadCards();
    void onLoadMoreButtonClick();
}
