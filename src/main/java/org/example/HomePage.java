package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;

public class HomePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ HomePage Loaded");

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

        HBox navbar = createNavbar(primaryStage, profilePic);

        VBox feedContainer = new VBox();
        feedContainer.setAlignment(Pos.TOP_CENTER);
        feedContainer.setPadding(new Insets(20));
        feedContainer.setSpacing(20);
        feedContainer.setStyle("-fx-background-color: #f0f0f0;");

        ScrollPane scrollPane = new ScrollPane(feedContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setStyle("-fx-background: transparent;");

        com.example.PostService.fetchPosts().thenAccept(posts ->
                Platform.runLater(() -> displayPosts(feedContainer, posts))
        );

        VBox mainLayout = new VBox(navbar, scrollPane);
        mainLayout.setSpacing(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        return new Scene(mainLayout, 800, 600);
    }

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

    private VBox createPostCard(com.example.Posts post) {
        VBox postCard = new VBox();
        postCard.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        postCard.setSpacing(10);
        postCard.setMaxWidth(600);

        HBox userSection = new HBox();
        userSection.setSpacing(10);
        userSection.setAlignment(Pos.CENTER_LEFT);

        String defaultProfilePic = "https://via.placeholder.com/40";
        ImageView userImageView = new ImageView();
        userImageView.setFitWidth(40);
        userImageView.setFitHeight(40);

        // Clip the user image to a circle
        Circle clip = new Circle(20, 20, 20);
        userImageView.setClip(clip);

        com.example.User postUser = post.getUser();
        String userProfilePic = (postUser != null && postUser.getProfilePic() != null) ? postUser.getProfilePic() : defaultProfilePic;

        try {
            Image image = new Image(userProfilePic, true);
            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    System.out.println("⚠️ Failed to load user profile image from URL: " + userProfilePic);
                    userImageView.setImage(new Image(defaultProfilePic, true));
                }
            });
            userImageView.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load user profile image. Using default.");
            userImageView.setImage(new Image(defaultProfilePic, true));
        }

// ➕ Add click-handler here:
        userImageView.setOnMouseClicked(event -> {
            Stage stage = (Stage) userImageView.getScene().getWindow();
            int receiverId = postUser.getId();
            com.example.ChatClientWindow chatClient = new com.example.ChatClientWindow();
            Scene chatScene = chatClient.createScene(stage, receiverId);
            stage.setScene(chatScene);
            stage.setTitle("Chat with " + postUser.getUsername());
            chatClient.connectWebSocket();
        });

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

        Label postBody = new Label(post.getBody());
        postBody.setWrapText(true);
        postBody.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        ImageView postImageView = new ImageView();
        postImageView.setFitWidth(600);
        postImageView.setPreserveRatio(true);

        if (post.getPhoto() != null) {
            try {
                Image postImage = new Image(post.getPhoto(), true);
                postImage.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        System.out.println("⚠️ Failed to load post image from URL: " + post.getPhoto());
                    }
                });
                postImageView.setImage(postImage);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load post image.");
            }
        }

        postCard.getChildren().addAll(userSection, postBody, postImageView);
        return postCard;
    }

    private HBox createNavbar(Stage primaryStage, String profilePic) {
        Label logoLabel = new Label("AL CRUITER");
        logoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #fff;");
        HBox logoBox = new HBox(logoLabel);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10));

        Button feedButton = createNavButton("Feed", primaryStage);
        Button notificationButton = createNavButton("Notifications", primaryStage);
        Button matchButton = createNavButton("Match", primaryStage);

        Button chatButton = new Button("Live Chat");
        chatButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        chatButton.setOnAction(e -> {
            Platform.runLater(() -> {
                try {
                    new com.example.JavaFXWebSocketClient().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(40);
        profileImageView.setFitHeight(40);

        // Clip profile image to circle
        Circle clip = new Circle(20, 20, 20);
        profileImageView.setClip(clip);

        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                Image image = new Image(profilePic, true);
                image.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        System.out.println("⚠️ Failed to load profile image from URL: " + profilePic);
                    }
                });
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load profile image.");
            }
        }

        ContextMenu profileMenu = new ContextMenu();
        MenuItem profileItem = new MenuItem("Profile");
        MenuItem logoutItem = new MenuItem("Logout");

        profileItem.setOnAction(e -> {
            com.example.User user = com.example.UserSessionManager.getUser();
            if (user != null && user.getChoose() != null) {
                primaryStage.setScene(new com.example.UserProfilePage().createScene(primaryStage));
            } else {
                primaryStage.setScene(new com.example.RolechoosePage().createScene(primaryStage));
            }
        });

        logoutItem.setOnAction(e -> {
            com.example.SessionManager.clearSession();
            primaryStage.setScene(new com.example.LoginPage().createScene(primaryStage));
        });

        profileMenu.getItems().addAll(profileItem, logoutItem);
        profileImageView.setOnMouseClicked(e -> profileMenu.show(profileImageView, e.getScreenX(), e.getScreenY()));

        HBox navBox = new HBox(20, feedButton, notificationButton, matchButton, chatButton, profileImageView);
        navBox.setAlignment(Pos.CENTER_RIGHT);
        navBox.setPadding(new Insets(10));

        HBox navbar = new HBox(logoBox, navBox);
        HBox.setHgrow(logoBox, Priority.ALWAYS);
        HBox.setHgrow(navBox, Priority.ALWAYS);
        navbar.setAlignment(Pos.CENTER);
        navbar.setSpacing(200);
        navbar.setStyle("-fx-background-color: #1E3A8A; -fx-padding: 10px;");

        return navbar;
    }

    private Button createNavButton(String text, Stage primaryStage) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        if (text.equals("Match")) {
            button.setOnAction(e -> primaryStage.setScene(new com.example.JobsPage().createScene(primaryStage)));
        }
        return button;
    }
}
