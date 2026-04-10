package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class DBConnection {
    private static final Dotenv dotenv = Dotenv.load();

    public static Connection getConnection() throws SQLException {
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        if (url == null){
            throw new RuntimeException("DB URL not set");
        }

        return DriverManager.getConnection(url, user, password);
    }

    public static Cloudinary cloudinary() {
//        System.out.println(dotenv.get("CLOUD_URL"));
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUD_URL"));
        return cloudinary;
    }
}
