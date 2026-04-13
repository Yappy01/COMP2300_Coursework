package utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import com.cloudinary.Cloudinary;

public class DBConnection {
    private static final Dotenv dotenv = Dotenv.load();
    private static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dotenv.get("DB_URL"));
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));

        config.setMaximumPoolSize(10); // 🔥 important
        config.setMinimumIdle(2);

        ds = new HikariDataSource(config);
    }


    public static Connection getConnection() throws SQLException {
        String url = dotenv.get("DB_URL");
        if (url == null){
            throw new RuntimeException("DB URL not set");
        }

        return ds.getConnection();
    }

    public static Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUD_URL"));
        return cloudinary;
    }
}
