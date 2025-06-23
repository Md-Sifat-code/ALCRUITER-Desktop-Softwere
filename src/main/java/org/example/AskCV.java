package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AskCV {

    public Scene createScene(Stage stage, String candidateId, String candidateName) {
        VBox chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setPrefWidth(680);

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        TextField inputField = new TextField();
        inputField.setPromptText("Ask something about " + candidateName + "'s CV...");
        inputField.setPrefWidth(600);

        Button sendButton = new Button("Send");

        HBox inputArea = new HBox(10, inputField, sendButton);
        inputArea.setPadding(new Insets(10));
        inputArea.setAlignment(Pos.CENTER);

        // 🔙 Back button
        Button backButton = new Button("🔙 Back");
        backButton.setStyle("-fx-font-size: 13px;");
        backButton.setOnAction(e -> {
            com.example.MatchCandidatesPage matchPage = new com.example.MatchCandidatesPage();
            Scene matchScene = matchPage.createScene(stage, Integer.parseInt(candidateId)); // assuming candidateId = postId
            stage.setScene(matchScene);
        });

        HBox topBar = new HBox(backButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_LEFT);

        VBox root = new VBox(topBar, scrollPane, inputArea);
        root.setPadding(new Insets(10));

        sendButton.setOnAction(e -> {
            String question = inputField.getText().trim();
            if (!question.isEmpty()) {
                addUserMessage(chatBox, question);
                inputField.clear();
                fetchAnswer(chatBox, candidateId, question);
            }
        });

        return new Scene(root, 700, 600);
    }

    private void addUserMessage(VBox chatBox, String message) {
        Label label = new Label(message);
        label.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 8; -fx-background-radius: 10;");
        HBox bubble = new HBox(label);
        bubble.setAlignment(Pos.CENTER_RIGHT);
        bubble.setPadding(new Insets(5));
        chatBox.getChildren().add(bubble);
    }

    private void addBotMessage(VBox chatBox, String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #ECECEC; -fx-padding: 8; -fx-background-radius: 10;");
        HBox bubble = new HBox(label);
        bubble.setAlignment(Pos.CENTER_LEFT);
        bubble.setPadding(new Insets(5));
        chatBox.getChildren().add(bubble);
    }

    private void fetchAnswer(VBox chatBox, String candidateId, String question) {
        try {
            String encodedQuestion = URLEncoder.encode(question, StandardCharsets.UTF_8);
            String url = "https://chakrihub-1-cilx.onrender.com/ai/cv/question/" + candidateId + "/" + encodedQuestion;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> Platform.runLater(() -> addBotMessage(chatBox, response)))
                    .exceptionally(e -> {
                        Platform.runLater(() -> addBotMessage(chatBox, "❌ Failed to get response."));
                        return null;
                    });
        } catch (Exception e) {
            addBotMessage(chatBox, "❌ Error: " + e.getMessage());
        }
    }
}
