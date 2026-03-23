package Controller;

import javafx.scene.control.Label;

public class CommentController {
    public Label comment;

    public void setComment(String commentText) {
        comment.setText(commentText);
    }
}
