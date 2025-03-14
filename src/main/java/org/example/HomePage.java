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

        // ✅ Feed Section with ScrollPane
        VBox feedContainer = new VBox();
        feedContainer.setAlignment(Pos.TOP_CENTER);
        feedContainer.setPadding(new Insets(20));
        feedContainer.setSpacing(20);
        feedContainer.setStyle("-fx-background-color: #f0f0f0;");

        ScrollPane scrollPane = new ScrollPane(feedContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setStyle("-fx-background: transparent;");

        // ✅ Fetch & Display Posts
        com.example.PostService.fetchPosts().thenAccept(posts ->
                Platform.runLater(() -> displayPosts(feedContainer, posts))
        );

        // ✅ Main Layout
        VBox mainLayout = new VBox(navbar, scrollPane);
        mainLayout.setSpacing(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        return new Scene(mainLayout, 800, 600);
    }

    // ✅ Display Posts in Feed Section
    private void displayPosts(VBox feedContainer, List<com.example.Posts> posts) {
        feedContainer.getChildren().clear();

        if (posts.isEmpty()) {
            feedContainer.getChildren().add(new Label("No posts available."));
            return;
        }

        for (com.example.Posts post : posts) {
            VBox postCard = createPostCard(post);
            feedContainer.getChildren().add(postCard);
        }
    }

    // ✅ Create Post Card UI with "Read More" Feature
    private VBox createPostCard(com.example.Posts post) {
        VBox postCard = new VBox();
        postCard.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10px; -fx-box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);");
        postCard.setSpacing(10);
        postCard.setMaxWidth(600);

        // ✅ User Info Section (Fix for Missing Data)
        HBox userSection = new HBox();
        userSection.setSpacing(10);
        userSection.setAlignment(Pos.CENTER_LEFT);

        // Default Profile Pic
        String defaultProfilePic = "https://via.placeholder.com/40";  // Placeholder image URL

        ImageView userImageView = new ImageView();
        userImageView.setFitWidth(40);
        userImageView.setFitHeight(40);
        userImageView.setStyle("-fx-border-radius: 50%;");

        com.example.User postUser = post.getUser(); // Get the user

        // ✅ If user exists, use their profile pic, else use default
        String userProfilePic = (postUser != null && postUser.getProfilePic() != null) ? postUser.getProfilePic() : defaultProfilePic;

        try {
            Image image = new Image(userProfilePic, true);
            userImageView.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load user profile image. Using default.");
            userImageView.setImage(new Image(defaultProfilePic, true));
        }

        // ✅ User Details
        VBox userInfo = new VBox();
        String username = (postUser != null && postUser.getUsername() != null) ? postUser.getUsername() : "Unknown User";
        String email = (postUser != null && postUser.getEmail() != null) ? postUser.getEmail() : "N/A";

        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label emailLabel = new Label(email);
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Label dateLabel = new Label(post.getCreatedDate());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

        userInfo.getChildren().addAll(usernameLabel, emailLabel, dateLabel);
        userSection.getChildren().addAll(userImageView, userInfo);

        // ✅ Post Body
        Label postBody = new Label(post.getBody());
        postBody.setWrapText(true);
        postBody.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        // ✅ Post Image
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

        // ✅ Add everything to post card
        postCard.getChildren().addAll(userSection, postBody, postImageView);
        return postCard;
    }


    // ✅ Helper Method to Limit Text to 4 Lines
    private String getShortText(String text, int lines) {
        String[] words = text.split(" ");
        StringBuilder shortText = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            shortText.append(word).append(" ");
            wordCount++;

            if (wordCount >= lines * 10) {  // Approximate words per line
                shortText.append("...");
                break;
            }
        }

        return shortText.toString();
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
}
