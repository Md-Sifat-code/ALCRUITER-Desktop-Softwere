package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserProfilePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ UserProfilePage Loaded");

        // ✅ Get User Data
        com.example.User user = com.example.UserSessionManager.getUser();
        if (user == null) {
            System.out.println("⚠️ No user found. Redirecting to HomePage.");
            return new com.example.HomePage().createScene(primaryStage);
        }

        Label titleLabel = new Label("User Profile");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ✅ Profile Picture
        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);

        String profilePic = (user.getProfilpic() != null) ? user.getProfilpic() : "https://via.placeholder.com/100";
        try {
            profileImageView.setImage(new Image(profilePic, true));
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load profile image.");
        }

        // ✅ User Info
        Label usernameLabel = new Label("Username: " + user.getUsername());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label roleLabel = new Label("Role: " + (user.getChoose() != null ? user.getChoose() : "Not Selected"));

        usernameLabel.setStyle("-fx-font-size: 16px;");
        emailLabel.setStyle("-fx-font-size: 16px;");
        roleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: blue;");

        // ✅ Back Button
        Button backButton = new Button("Back to Home");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        backButton.setOnAction(event -> primaryStage.setScene(new com.example.HomePage().createScene(primaryStage)));

        VBox layout = new VBox(20, titleLabel, profileImageView, usernameLabel, emailLabel, roleLabel, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        return new Scene(layout, 400, 400);
    }
}
