package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class RecruiterSessionManager {

    private static com.example.Recruiter recruiter;

    public static void fetchRecruiterData() {
        String username = com.example.SessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            System.out.println("⚠️ No username found in session.");
            return;
        }

        String apiUrl = "https://chakrihub-0qv1.onrender.com/User/search/" + username;
        System.out.println("🔵 Fetching recruiter data from: " + apiUrl);

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
                if (!jsonResponse.isNull("recruter")) {
                    JSONObject recruiterJson = jsonResponse.getJSONObject("recruter");
                    recruiter = new com.example.Recruiter(recruiterJson);
                    System.out.println("✅ Recruiter Data Stored: " + recruiter.getName());
                } else {
                    System.out.println("⚠️ No recruiter info found in user data.");
                }
            } else {
                System.out.println("❌ Failed to fetch recruiter data. HTTP Status: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("⚠️ Error fetching recruiter data: " + e.getMessage());
        }
    }

    public static com.example.Recruiter getRecruiter() {
        return recruiter;
    }
}
