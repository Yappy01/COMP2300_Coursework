package utils;

import Models.User;

public class Session {

    private static Session instance; // single session
    private User user;

    // private constructor (important for singleton)
    private Session(User user) {
        this.user = user;
    }

    // 🔑 Create session (login)
    public static void startSession(User user) {
        instance = new Session(user);
    }

    // 🔍 Get current session
    public static Session getInstance() {
        return instance;
    }

    // 👤 Get logged-in user
    public User getUser() {
        return user;
    }

    // Get userID
    public int getUserID() {return user.getUserId();}

    // Get username
    public String getUserName() { return user.getName();}

    // 🚪 End session (logout)
    public static void endSession() {
        instance = null;
    }

    // ✅ Check if logged in
    public static boolean isLoggedIn() {
        return instance != null;
    }
}