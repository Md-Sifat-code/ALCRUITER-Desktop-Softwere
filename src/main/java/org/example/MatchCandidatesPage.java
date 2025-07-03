package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
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
        layout.getStyleClass().add("root");

        Label title = new Label("🎯 Matched Candidates for Post ID: " + postId);
        title.getStyleClass().add("title-label");

        layout.getChildren().add(title);

        // ScrollPane for dynamic cards
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        VBox mainContainer = new VBox(10);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(15));
        mainContainer.getChildren().add(scrollPane);

        // ✅ Back Button Section
        Button backButton = new Button("⬅ Back");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            primaryStage.setScene(new com.example.JobsPage().createScene(primaryStage));
        });

        mainContainer.getChildren().add(backButton);

        Scene scene = new Scene(mainContainer, 700, 600);

        // Load CSS
        java.net.URL stylesheetURL = getClass().getResource("css/match_candidates.css");
        if (stylesheetURL != null) {
            scene.getStylesheets().add(stylesheetURL.toExternalForm());
        } else {
            System.err.println("❌ Error: Stylesheet 'css/match_candidates.css' not found. Check resources folder.");
        }

        fetchMatches(postId, layout, primaryStage);

        return scene;
    }

    private void fetchMatches(int postId, VBox container, Stage stage) {
        String url = "https://chakrihub-0qv1.onrender.com/api/v1/recruiter/suggestions/" + postId;
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
            String cvSummary = obj.optString("cvSummery", "No summary provided.");

            StringBuilder skillsList = new StringBuilder();
            for (int j = 0; j < skillsArray.length(); j++) {
                skillsList.append(skillsArray.getString(j));
                if (j < skillsArray.length() - 1) skillsList.append(", ");
            }

            VBox card = new VBox(5);
            card.getStyleClass().add("card");

            Label nameLabel = new Label("👤 " + candidateName + " (@" + username + ")");
            nameLabel.getStyleClass().addAll("card-label", "bold");

            Label matchLabel = new Label("Match %: " + matchPercentage);
            matchLabel.getStyleClass().add("card-label");

            Label skillsLabel = new Label("Matched Skills: " + skillsList);
            skillsLabel.getStyleClass().add("card-label");

            // ✅ CV Summary section
            Label cvSummaryLabel = new Label("📄 CV Summary: " + cvSummary);
            cvSummaryLabel.getStyleClass().add("card-label");
            cvSummaryLabel.setWrapText(true); // in case summary is long

            Button matchButton = new Button("✅ Ask CV");
            matchButton.getStyleClass().add("ask-cv-button");
            matchButton.setOnAction(e -> {
                com.example.AskCV askCV = new com.example.AskCV();
                Scene askCVScene = askCV.createScene(stage, candidateId, candidateName);
                stage.setScene(askCVScene);
            });

            Button chatButton = new Button("💬 Let's Chat");
            chatButton.getStyleClass().add("chat-button");
            chatButton.setOnAction(e -> {
                com.example.JavaFXWebSocketClient chatClient = new com.example.JavaFXWebSocketClient();
                Scene chatScene = chatClient.createScene(stage, username);
                stage.setScene(chatScene);
                stage.setTitle("Chat with @" + username);
                chatClient.connectWebSocket();
            });

            HBox buttonsBox = new HBox(10, matchButton, chatButton);
            buttonsBox.setAlignment(Pos.CENTER_LEFT);

            card.getChildren().addAll(nameLabel, matchLabel, skillsLabel, cvSummaryLabel, buttonsBox);
            container.getChildren().add(card);
        }
    }
}
