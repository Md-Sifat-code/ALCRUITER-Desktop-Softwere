package com.example;

public class SessionManager {
    private static String token;
    private static String username;
    private static String email;
    private static String[] roles;

    // Set the session data
    public static void setSession(String token, String username, String email, String[] roles) {
        SessionManager.token = token;
        SessionManager.username = username;
        SessionManager.email = email;
        SessionManager.roles = roles;
    }

    // Get the token
    public static String getToken() {
        return token;
    }

    // Get the username
    public static String getUsername() {
        return username;
    }

    // Get the email
    public static String getEmail() {
        return email;
    }

    // Get the roles
    public static String[] getRoles() {
        return roles;
    }

    // Clear the session
    public static void clearSession() {
        token = null;
        username = null;
        email = null;
        roles = null;
    }

    // Check if user is logged in
    public static boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }
}
