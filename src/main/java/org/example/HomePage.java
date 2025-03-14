package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ HomePage Loaded");

        // Get user data from UserSessionManager
        com.example.User user = com.example.UserSessionManager.getUser();

        if (user == null) {
            System.out.println("⚠️ User data not found. Fetching again...");
            com.example.UserSessionManager.fetchUserData(); // Fetch data if not available
            user = com.example.UserSessionManager.getUser();
        }

        String username = (user != null && user.getUsername() != null) ? user.getUsername() : "User";
        String email = (user != null && user.getEmail() != null) ? user.getEmail() : "N/A";
        String profilePic = (user != null && user.getProfilePic() != null) ? user.getProfilePic() : null;
        String choose = (user != null && user.getChoose() != null) ? user.getChoose() : "Not Selected";

        System.out.println("🔹 Username: " + username);
        System.out.println("🔹 Email: " + email);
        System.out.println("🔹 Profile Pic: " + profilePic);
        System.out.println("🔹 Role Selection: " + choose);

        // Labels
        Label welcomeLabel = new Label("Welcome, " + username + "!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label emailLabel = new Label("Email: " + email);
        Label chooseLabel = new Label("Role Selection: " + choose);

        // Profile Picture
        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(100); // Set width
        profileImageView.setFitHeight(100); // Set height

        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                Image image = new Image(profilePic, true);
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load profile image: " + e.getMessage());
            }
        }

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            System.out.println("🔴 Logging out...");
            com.example.SessionManager.clearSession();
            navigateToLogin(primaryStage);
        });

        VBox vbox = new VBox(15, profileImageView, welcomeLabel, emailLabel, chooseLabel, logoutButton);
        vbox.setAlignment(Pos.CENTER);

        return new Scene(new StackPane(vbox), 600, 400);
    }

    private void navigateToLogin(Stage primaryStage) {
        System.out.println("🔵 Navigating to Login Page...");
        Scene loginScene = new com.example.LoginPage().createScene(primaryStage);
        primaryStage.setScene(loginScene);
    }
}
