package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class HomePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ HomePage Loaded");

        // ✅ Get user data
        com.example.User user = com.example.UserSessionManager.getUser();

        if (user == null) {
            System.out.println("⚠️ User data not found. Fetching again...");
            com.example.UserSessionManager.fetchUserData();
            user = com.example.UserSessionManager.getUser();
        }

        String username = (user != null && user.getUsername() != null) ? user.getUsername() : "User";
        String email = (user != null && user.getEmail() != null) ? user.getEmail() : "N/A";
        String profilePic = (user != null && user.getProfilePic() != null) ? user.getProfilePic() : null;

        System.out.println("🔹 Username: " + username);
        System.out.println("🔹 Email: " + email);
        System.out.println("🔹 Retrieved Profile Pic: " + profilePic);

        // ✅ Navbar (Keeping Your Design)
        HBox navbar = createNavbar(primaryStage, profilePic);

        // ✅ Feed Section
        VBox feedSection = new VBox();
        feedSection.setPrefHeight(600);
        feedSection.setAlignment(Pos.TOP_CENTER);
        feedSection.setPadding(new Insets(20));
        feedSection.setSpacing(20);
        feedSection.setStyle("-fx-background-color: #f0f0f0;");

        // ✅ Fetch & Display Posts
        com.example.PostService.fetchPosts().thenAccept(posts ->
                Platform.runLater(() -> displayPosts(feedSection, posts))
        );

        // ✅ Main Layout
        VBox mainLayout = new VBox(navbar, feedSection);
        mainLayout.setSpacing(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        return new Scene(mainLayout, 800, 600);
    }

    // ✅ Display Posts in Feed Section
    private void displayPosts(VBox feedSection, List<com.example.Posts> posts) {
        feedSection.getChildren().clear();

        if (posts.isEmpty()) {
            feedSection.getChildren().add(new Label("No posts available."));
            return;
        }

        for (com.example.Posts post : posts) {
            VBox postCard = createPostCard(post);
            feedSection.getChildren().add(postCard);
        }
    }

    // ✅ Create Post Card UI
    private VBox createPostCard(com.example.Posts post) {
        VBox postCard = new VBox();
        postCard.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10px; -fx-box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);");
        postCard.setSpacing(10);
        postCard.setMaxWidth(600);

        // User Info Section
        HBox userSection = new HBox();
        userSection.setSpacing(10);
        userSection.setAlignment(Pos.CENTER_LEFT);

        ImageView userImageView = new ImageView();
        userImageView.setFitWidth(40);
        userImageView.setFitHeight(40);
        userImageView.setStyle("-fx-border-radius: 50%;");

        if (post.getUser() != null && post.getUser().getProfilePic() != null) {
            try {
                Image image = new Image(post.getUser().getProfilePic(), true);
                userImageView.setImage(image);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load user profile image.");
            }
        }

        VBox userInfo = new VBox();
        Label usernameLabel = new Label(post.getUser() != null ? post.getUser().getUsername() : "Unknown User");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label emailLabel = new Label(post.getUser() != null ? post.getUser().getEmail() : "N/A");
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Label dateLabel = new Label(post.getCreatedDate());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

        userInfo.getChildren().addAll(usernameLabel, emailLabel, dateLabel);
        userSection.getChildren().addAll(userImageView, userInfo);

        // Post Body
        Label postBody = new Label(post.getBody());
        postBody.setWrapText(true);
        postBody.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        // Post Image
        ImageView postImageView = new ImageView();
        postImageView.setFitWidth(600);
        postImageView.setPreserveRatio(true);

        if (post.getPhoto() != null) {
            try {
                Image postImage = new Image(post.getPhoto(), true);
                postImageView.setImage(postImage);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load post image.");
            }
        }

        // Add elements to post card
        postCard.getChildren().addAll(userSection, postBody, postImageView);

        return postCard;
    }

    // ✅ Navbar (Keeping Your Existing Design)
    private HBox createNavbar(Stage primaryStage, String profilePic) {
        Label logoLabel = new Label("AL CRUITER");
        logoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #fff;");

        HBox logoBox = new HBox(logoLabel);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10));

        Button feedButton = createNavButton("Feed", primaryStage);
        Button notificationButton = createNavButton("Notifications", primaryStage);
        Button matchButton = createNavButton("Match", primaryStage);

        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(40);
        profileImageView.setFitHeight(40);
        profileImageView.setStyle("-fx-border-radius: 50%;");

        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                Image image = new Image(profilePic, true);
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load profile image.");
            }
        }

        ContextMenu profileMenu = new ContextMenu();
        MenuItem profileOption = new MenuItem("Profile");
        MenuItem logoutOption = new MenuItem("Logout");

        profileOption.setOnAction(e -> System.out.println("👤 Navigating to Profile Page..."));
        logoutOption.setOnAction(e -> {
            System.out.println("🔴 Logging out...");
            com.example.SessionManager.clearSession();
            navigateToLogin(primaryStage);
        });

        profileMenu.getItems().addAll(profileOption, logoutOption);
        profileImageView.setOnMouseClicked(e -> profileMenu.show(profileImageView, e.getScreenX(), e.getScreenY()));

        HBox navBox = new HBox(20, feedButton, notificationButton, matchButton, profileImageView);
        navBox.setAlignment(Pos.CENTER_RIGHT);
        navBox.setPadding(new Insets(10));

        HBox navbar = new HBox(logoBox, navBox);
        navbar.setHgrow(logoBox, Priority.ALWAYS);
        navbar.setHgrow(navBox, Priority.ALWAYS);
        navbar.setAlignment(Pos.CENTER);
        navbar.setSpacing(200);
        navbar.setStyle("-fx-background-color: #1E3A8A; -fx-padding: 10px;");

        return navbar;
    }

    private Button createNavButton(String text, Stage primaryStage) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        return button;
    }

    private void navigateToLogin(Stage primaryStage) {
        System.out.println("🔵 Navigating to Login Page...");
        Scene loginScene = new com.example.LoginPage().createScene(primaryStage);
        primaryStage.setScene(loginScene);
    }
}
