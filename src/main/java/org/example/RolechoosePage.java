package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RolechoosePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ RolechoosePage Loaded");

        // Title
        Label titleLabel = new Label("Choose Your Role");
        titleLabel.getStyleClass().add("title-label");

        // Role Buttons
        Button candidateButton = new Button("Candidate");
        candidateButton.getStyleClass().add("role-button");

        Button recruiterButton = new Button("Recruiter");
        recruiterButton.getStyleClass().add("role-button");

        // Back Button
        Button backButton = new Button("⬅ Back");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            primaryStage.setScene(new com.example.LoginPage().createScene(primaryStage)); // Or wherever you want to go back
        });

        HBox buttonBox = new HBox(20, candidateButton, recruiterButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, titleLabel, buttonBox, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("role-choose-root");

        // Button Actions
        candidateButton.setOnAction(event -> {
            updateUserRole("Candidate");
            primaryStage.setScene(new com.example.CandidateAdd().createScene(primaryStage));
        });

        recruiterButton.setOnAction(event -> {
            updateUserRole("Recruiter");
            primaryStage.setScene(new com.example.RecruiterAdd().createScene(primaryStage));
        });

        Scene scene = new Scene(layout, 450, 300);

        // Load CSS
        try {
            var css = getClass().getResource("css/rolechoose.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            } else {
                System.err.println("❌ Stylesheet 'css/rolechoose.css' not found.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading CSS: " + e.getMessage());
        }

        return scene;
    }

    private void updateUserRole(String role) {
        System.out.println("🔹 Role Selected: " + role);

        com.example.User user = com.example.UserSessionManager.getUser();
        if (user != null) {
            user = new com.example.User(user.getId(), user.getUsername(), user.getProfilpic(),
                    user.getEmail(), user.getCandidate(), user.getRecruter(), user.getPosts(), role);
            System.out.println("✅ User role updated: " + role);
        } else {
            System.out.println("⚠️ User not found in session.");
        }
    }
}
