package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class CandidateSessionManager {

    private static com.example.Candidate candidate;

    public static void fetchCandidateData() {
        String username = com.example.SessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            System.out.println("⚠️ No username found in session.");
            return;
        }

        String apiUrl = "https://chakrihub-1-cilx.onrender.com/User/search/" + username;
        System.out.println("🔵 Fetching candidate data from: " + apiUrl);

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
                if (!jsonResponse.isNull("candidate")) {
                    JSONObject candidateJson = jsonResponse.getJSONObject("candidate");
                    candidate = new com.example.Candidate(candidateJson);
                    System.out.println("✅ Candidate Data Stored: " + candidate.getFullName());
                } else {
                    System.out.println("⚠️ No candidate info found in user data.");
                }
            } else {
                System.out.println("❌ Failed to fetch candidate data. HTTP Status: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("⚠️ Error fetching candidate data: " + e.getMessage());
        }
    }

    public static com.example.Candidate getCandidate() {
        return candidate;
    }
}
