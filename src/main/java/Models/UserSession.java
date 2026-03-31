package Models;

public class UserSession {
    private static UserSession instance;

    private int userId;
    private String userName;

    // Private constructor prevents other classes from creating new sessions
    private UserSession(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // This is how you "Login" and start the session
    public static void setInstance(int userId, String userName) {
        instance = new UserSession(userId, userName);
    }

    // This is how you access the session from any Controller
    public static UserSession getInstance() {
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    // Use this for Logout
    public void cleanUserSession() {
        userId = 0;
        userName = null;
        instance = null;
    }
}