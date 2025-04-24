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

        String apiUrl = "https://chakrihub-r7m5.onrender.com/User/search/" + username;
        System.out.println("🔵 Fetching user data from: " + apiUrl);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                System.out.println("📥 User Data Received: " + jsonResponse.toString(2)); // Debugging
                user = new com.example.User(jsonResponse);
                System.out.println("✅ User Data Stored: " + user.getUsername());
            } else {
                System.out.println("❌ Failed to fetch user data. HTTP Status: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("⚠️ Error fetching user data: " + e.getMessage());
        }
    }

    public static com.example.User getUser() {
        return user;
    }
}
