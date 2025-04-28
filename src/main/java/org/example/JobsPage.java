package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class JobsPage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ JobsPage Loaded");

        // ✅ Get logged-in user
        com.example.User user = com.example.UserSessionManager.getUser();
        String profilePic = (user != null && user.getProfilePic() != null) ? user.getProfilePic() : null;

        // ✅ Add Navbar
        HBox navbar = createNavbar(primaryStage, profilePic);

        VBox jobFeedContainer = new VBox();
        jobFeedContainer.setAlignment(Pos.TOP_CENTER);
        jobFeedContainer.setPadding(new Insets(20));
        jobFeedContainer.setSpacing(20);
        jobFeedContainer.setStyle("-fx-background-color: #f0f0f0;");

        ScrollPane scrollPane = new ScrollPane(jobFeedContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setStyle("-fx-background: transparent;");

        // ✅ Fetch Jobs
        int userId = (user != null) ? user.getId() : -1;
        if (userId != -1) {
            fetchUserJobs(userId, jobFeedContainer);
        }

        VBox mainLayout = new VBox(navbar, scrollPane);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(10);

        return new Scene(mainLayout, 800, 600);
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

        // ✅ Dropdown Menu for Profile Image
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
            System.out.println("🔴 Logging out...");
            com.example.SessionManager.clearSession();
            primaryStage.setScene(new com.example.LoginPage().createScene(primaryStage));
        });

        profileMenu.getItems().addAll(profileItem, logoutItem);

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

        if (text.equals("Match")) {
            button.setOnAction(e -> primaryStage.setScene(new com.example.JobsPage().createScene(primaryStage)));
        }

        return button;
    }


    // ✅ Fetch Jobs from API
    private void fetchUserJobs(int userId, VBox jobFeedContainer) {
        String apiUrl = "https://chakrihub-1-sgbz.onrender.com/Post/user/" + userId;
        System.out.println("🔵 Fetching jobs from: " + apiUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> Platform.runLater(() -> displayJobs(response, jobFeedContainer)))
                .exceptionally(e -> {
                    System.out.println("❌ Error fetching jobs: " + e.getMessage());
                    return null;
                });
    }

    // ✅ Display Jobs in the Feed with Styled Cards
    private void displayJobs(String response, VBox jobFeedContainer) {
        jobFeedContainer.getChildren().clear();

        JSONArray jobArray = new JSONArray(response);
        if (jobArray.isEmpty()) {
            jobFeedContainer.getChildren().add(new Label("No jobs available."));
            return;
        }

        for (int i = 0; i < jobArray.length(); i++) {
            JSONObject jobObject = jobArray.getJSONObject(i);
            int jobId = jobObject.getInt("id");

            fetchJobDetails(jobId, jobFeedContainer);
        }
    }

    // ✅ Fetch Job Details for a specific job ID
    private void fetchJobDetails(int jobId, VBox jobFeedContainer) {
        String jobDetailsUrl = "https://chakrihub-1-sgbz.onrender.com/Post/" + jobId;
        System.out.println("🔵 Fetching job details from: " + jobDetailsUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jobDetailsUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> Platform.runLater(() -> displayJobCard(response, jobFeedContainer)))
                .exceptionally(e -> {
                    System.out.println("❌ Error fetching job details: " + e.getMessage());
                    return null;
                });
    }

    // ✅ Create and Display Job Card with Recruiter Info
    private void displayJobCard(String response, VBox jobFeedContainer) {
        JSONObject jobObject = new JSONObject(response);

        // ✅ Extract job details
        String description = jobObject.optString("body", "No Description Available");
        String imageUrl = jobObject.optString("photo", null);
        String createdDate = jobObject.optString("createdDate", "Unknown Date");

        // ✅ Recruiter details
        JSONObject userObject = jobObject.optJSONObject("user");
        String recruiterName = "Unknown";
        String recruiterEmail = "N/A";
        String recruiterProfilePic = null;

        if (userObject != null) {
            recruiterEmail = userObject.optString("email", "N/A");
            recruiterProfilePic = userObject.optString("profilpic", null);

            JSONObject recruiterObject = userObject.optJSONObject("recruter");
            if (recruiterObject != null) {
                recruiterName = recruiterObject.optString("name", "Unknown");
            }
        }

        // ✅ Create Job Card
        VBox jobCard = new VBox();
        jobCard.setStyle("""
            -fx-background-color: white;
            -fx-padding: 15px;
            -fx-border-radius: 10px;
            -fx-border-color: #ddd;
            -fx-border-width: 1px;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);
        jobCard.setSpacing(10);
        jobCard.setMaxWidth(600);

        // ✅ Job Description
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        // ✅ Job Image (if available)
        ImageView jobImageView = new ImageView();
        jobImageView.setFitWidth(600);
        jobImageView.setPreserveRatio(true);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image jobImage = new Image(imageUrl, true);
                jobImageView.setImage(jobImage);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load job image.");
            }
        }

        // ✅ Recruiter Info
        Label recruiterLabel = new Label("Recruiter: " + recruiterName);
        recruiterLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");

        Label emailLabel = new Label("Email: " + recruiterEmail);
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

        ImageView recruiterProfileImageView = new ImageView();
        recruiterProfileImageView.setFitWidth(50);
        recruiterProfileImageView.setFitHeight(50);

        if (recruiterProfilePic != null && !recruiterProfilePic.isEmpty()) {
            try {
                Image recruiterImage = new Image(recruiterProfilePic, true);
                recruiterProfileImageView.setImage(recruiterImage);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load recruiter profile picture.");
            }
        }

        // ✅ Recruiter Info Layout
        HBox recruiterInfoBox = new HBox(10, recruiterProfileImageView, recruiterLabel, emailLabel);
        recruiterInfoBox.setAlignment(Pos.CENTER_LEFT);

        // ✅ Add elements to job card
        jobCard.getChildren().addAll(recruiterInfoBox, descLabel, jobImageView, new Label("Posted on: " + createdDate));

        jobFeedContainer.getChildren().add(jobCard);
    }
}
//oay
