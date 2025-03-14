package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class UserSessionManager {
    private static com.example.User user;

    public static void fetchUserData() {
        String username = com.example.SessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            System.out.println("⚠️ No username found in session.");
            return;
        }

        String apiUrl = "https://chakrihub-mhh5.onrender.com/User/search/" + username;
        System.out.println("🔵 Fetching user data from: " + apiUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).header("Content-Type", "application/json").GET().build();
        HttpClient client = HttpClient.newHttpClient();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() == 200) {
                user = new com.example.User(new JSONObject(response.body()));
                System.out.println("✅ User Data Stored: " + user.getUsername());
            }
        });
    }

    public static com.example.User getUser() { return user; }
}
