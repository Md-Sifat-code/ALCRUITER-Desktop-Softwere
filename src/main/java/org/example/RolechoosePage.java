package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RolechoosePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ RolechoosePage Loaded");

        Label titleLabel = new Label("Choose Your Role");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button candidateButton = new Button("Candidate");
        Button recruiterButton = new Button("Recruiter");

        candidateButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        recruiterButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        candidateButton.setOnAction(event -> {
            updateUserRole("Candidate");
            primaryStage.setScene(new com.example.UserProfilePage().createScene(primaryStage));
        });

        recruiterButton.setOnAction(event -> {
            updateUserRole("Recruiter");
            primaryStage.setScene(new com.example.UserProfilePage().createScene(primaryStage));
        });

        VBox layout = new VBox(20, titleLabel, candidateButton, recruiterButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        return new Scene(layout, 400, 300);
    }

    private void updateUserRole(String role) {
        System.out.println("🔹 Role Selected: " + role);

        // ✅ Update user role in session
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
