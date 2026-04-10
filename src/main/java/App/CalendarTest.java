package App;

import Service.UserService;
import com.cloudinary.Cloudinary;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.Session;

public class CalendarTest extends Application {

    private final UserService userService = new UserService();
    @Override
    public void start(Stage primaryStage) throws Exception {
        Session.startSession(userService.searchByUsername("jiyou"));

//        Cloudinary cloud = DBConnection.cloudinary();
//
//        import com.cloudinary.Cloudinary;
//        import com.cloudinary.utils.ObjectUtils;
//        import java.util.Map;
//
//// Use the Cloudinary instance you already created from your .env
//        Map uploadResult = cloudinary.uploader().upload("path/to/your/image.jpg", ObjectUtils.emptyMap());
//        System.out.println(uploadResult.get("secure_url")); // prints the uploaded image URL
//
//        Map uploadResult = cloudinary.uploader().upload("path/to/your/image.jpg", ObjectUtils.emptyMap());
//        String imageUrl = (String) uploadResult.get("secure_url"); // or "url"
//        System.out.println(imageUrl);

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages/UserProfile.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Custom JavaFX Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}