package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MatchCandidatesPage {

    public Scene createScene(Stage primaryStage, int postId) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🎯 Matched Candidates for Post ID: " + postId);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        layout.getChildren().add(title);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        fetchMatches(postId, layout, primaryStage);

        return new Scene(scrollPane, 700, 600);
    }

    private void fetchMatches(int postId, VBox container, Stage stage) {
        String url = "https://chakrihub-1-cilx.onrender.com/api/v1/recruiter/suggestions/" + postId;
        System.out.println("🔎 Fetching matches from: " + url);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> Platform.runLater(() -> displayMatches(response, container, stage)))
                .exceptionally(e -> {
                    Platform.runLater(() -> container.getChildren().add(new Label("❌ Failed to fetch matches.")));
                    System.out.println("❌ Error: " + e.getMessage());
                    return null;
                });
    }

    private void displayMatches(String jsonResponse, VBox container, Stage stage) {
        JSONArray array = new JSONArray(jsonResponse);

        if (array.isEmpty()) {
            container.getChildren().add(new Label("No matching candidates found."));
            return;
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            String candidateName = obj.optString("candidateName", "N/A");
            String username = obj.optString("username", "N/A");
            String candidateId = obj.optString("candidateId", "0");
            double matchPercentage = obj.optDouble("matchPercentage", 0);
            JSONArray skillsArray = obj.optJSONArray("matchedSkills");

            StringBuilder skillsList = new StringBuilder();
            for (int j = 0; j < skillsArray.length(); j++) {
                skillsList.append(skillsArray.getString(j));
                if (j < skillsArray.length() - 1) skillsList.append(", ");
            }

            VBox card = new VBox(5);
            card.setStyle("""
                -fx-background-color: #fff;
                -fx-padding: 15;
                -fx-border-color: #ddd;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);
            """);

            Label nameLabel = new Label("👤 " + candidateName + " (@" + username + ")");
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label matchLabel = new Label("Match %: " + matchPercentage);
            Label skillsLabel = new Label("Matched Skills: " + skillsList);

            Button matchButton = new Button("✅ Ask CV");
            matchButton.setStyle("""
                -fx-background-color: #4CAF50;
                -fx-text-fill: white;
                -fx-padding: 5 10;
                -fx-font-size: 13px;
                -fx-border-radius: 4;
            """);

            matchButton.setOnAction(e -> {
                com.example.AskCV askCV = new com.example.AskCV();
                Scene askCVScene = askCV.createScene(stage, candidateId, candidateName);
                stage.setScene(askCVScene);
            });

            card.getChildren().addAll(nameLabel, matchLabel, skillsLabel, matchButton);
            container.getChildren().add(card);
        }
    }
}
