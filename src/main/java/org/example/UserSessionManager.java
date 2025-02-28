package com.example;

public class UserSessionManager {
    private static com.example.User user;  // Store user information

    // Set the user session details
    public static void setUserSession(int id, String username, String email, String profilpic,
                                      Object candidate, Object recruter, String[] posts, Object choose) {
        user = new com.example.User(id, username, email, profilpic, candidate, recruter, posts, choose);
    }

    // Get the current logged-in user
    public static com.example.User getUser() {
        return user;
    }

    // Clear the user session (useful for logout)
    public static void clearUserSession() {
        user = null;
    }

    // Check if user is logged in
    public static boolean isUserLoggedIn() {
        return user != null;
    }
}
