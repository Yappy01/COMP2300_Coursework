package utils;

import DBHandling.ComPostDatabase;
import Models.Post;
import Models.User;

import java.util.List;

public class Session {

    private static Session instance; // single session
    private User user;
    private List<Post> allPosts;
    private List<Post> likedPosts;

    // private constructor (important for singleton)
    private Session(User user) {
        this.user = user;
    }

    public List<Post> getAllPosts(String type) {
        this.allPosts = ComPostDatabase.getCard(12, type);
        return this.allPosts;
    }

    public List<Post> getLikedPosts() {
        if (this.allPosts == null) {
            this.allPosts = ComPostDatabase.getRecentLiked(Session.getInstance().getUser().getUserId(), 12);
        }
        return this.allPosts;
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

    // 🚪 End session (logout)
    public static void endSession() {
        instance = null;
    }

    // ✅ Check if logged in
    public static boolean isLoggedIn() {
        return instance != null;
    }
}