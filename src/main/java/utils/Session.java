package utils;

import DBHandling.ComPostDatabase;
import Models.Post;
import Models.User;

import java.util.List;

public class Session {

    private static Session instance; // single session
    private User user;
    private List<Post> allPosts;

    // private constructor (important for singleton)
    private Session(User user) {
        this.user = user;
    }

    public List<Post> getAllPosts() {
        if (this.allPosts == null) {
            System.out.println("pulling");
            this.allPosts = ComPostDatabase.getRecent(12);
        }
        System.out.println("doneeee");
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