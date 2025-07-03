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
        chatBox.getStyleClass().add("chat-box");

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.getStyleClass().add("chat-scroll");

        TextField inputField = new TextField();
        inputField.setPromptText("Ask something about " + candidateName + "'s CV...");
        inputField.setPrefWidth(600);
        inputField.getStyleClass().add("chat-input");

        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");

        HBox inputArea = new HBox(10, inputField, sendButton);
        inputArea.setPadding(new Insets(10));
        inputArea.setAlignment(Pos.CENTER);
        inputArea.getStyleClass().add("input-area");

        // 🔙 Back button
        Button backButton = new Button("⬅ Back");
        backButton.setOnAction(e -> {
            com.example.MatchCandidatesPage matchPage = new com.example.MatchCandidatesPage();
            Scene matchScene = matchPage.createScene(stage, Integer.parseInt(candidateId));
            stage.setScene(matchScene);
        });
        backButton.getStyleClass().add("back-button");

        HBox topBar = new HBox(backButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.getStyleClass().add("top-bar");

        VBox root = new VBox(topBar, scrollPane, inputArea);
        root.setPadding(new Insets(10));
        root.getStyleClass().add("askcv-root");

        sendButton.setOnAction(e -> {
            String question = inputField.getText().trim();
            if (!question.isEmpty()) {
                addUserMessage(chatBox, question);
                inputField.clear();
                fetchAnswer(chatBox, candidateId, question);
            }
        });

        Scene scene = new Scene(root, 700, 600);

        // ✅ Load CSS
        java.net.URL cssURL = getClass().getResource("css/askcv.css");
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
        } else {
            System.err.println("❌ Error: Stylesheet 'css/askcv.css' not found.");
        }

        return scene;
    }

    private void addUserMessage(VBox chatBox, String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.getStyleClass().add("user-message");

        HBox bubble = new HBox(label);
        bubble.setAlignment(Pos.CENTER_RIGHT);
        bubble.setPadding(new Insets(5));
        chatBox.getChildren().add(bubble);
    }

    private void addBotMessage(VBox chatBox, String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.getStyleClass().add("bot-message");

        HBox bubble = new HBox(label);
        bubble.setAlignment(Pos.CENTER_LEFT);
        bubble.setPadding(new Insets(5));
        chatBox.getChildren().add(bubble);
    }

    private void fetchAnswer(VBox chatBox, String candidateId, String question) {
        try {
            String encodedQuestion = URLEncoder.encode(question, StandardCharsets.UTF_8);
            String url = "https://chakrihub-0qv1.onrender.com/ai/cv/question/" + candidateId + "/" + encodedQuestion;

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
