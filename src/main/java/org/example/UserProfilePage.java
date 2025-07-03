package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class UserProfilePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ UserProfilePage Loaded");

        com.example.User user = com.example.UserSessionManager.getUser();
        if (user == null) {
            System.out.println("⚠️ No user found. Redirecting to HomePage.");
            return new com.example.HomePage().createScene(primaryStage);
        }

        // --- Main Layout ---
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        // --- Header ---
        Label titleLabel = new Label("User Profile");
        titleLabel.setId("title-label");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20));
        root.setTop(titleBox);

        // --- Scrollable Content ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        VBox contentLayout = new VBox(20);
        contentLayout.setAlignment(Pos.TOP_CENTER);
        contentLayout.setPadding(new Insets(20, 40, 20, 40));
        contentLayout.getStyleClass().add("content-layout");

        // --- Profile Header Section ---
        VBox profileHeader = new VBox(10);
        profileHeader.setAlignment(Pos.CENTER);

        ImageView profileImageView = new ImageView();
        if (user.getProfilpic() != null && !user.getProfilpic().isEmpty()) {
            profileImageView.setImage(new Image(user.getProfilpic(), true));
        } else {
            // Default placeholder image if none is available
            profileImageView.setImage(new Image("https://via.placeholder.com/100", true));
        }
        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);
        Circle clip = new Circle(50, 50, 50);
        profileImageView.setClip(clip);

        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("username-label");

        Label roleLabel = new Label(user.getChoose());
        roleLabel.getStyleClass().add("role-label");

        profileHeader.getChildren().addAll(profileImageView, usernameLabel, roleLabel);
        contentLayout.getChildren().add(profileHeader);
        contentLayout.getChildren().add(new Separator());

        // --- Details Section using GridPane for alignment ---
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(12);
        detailsGrid.getStyleClass().add("details-grid");
        int rowIndex = 0;

        // Basic Info
        detailsGrid.add(createLabel("Email:"), 0, rowIndex);
        detailsGrid.add(new Label(user.getEmail()), 1, rowIndex++);

        // --- Role-Specific Information ---
        if ("candidate".equalsIgnoreCase(user.getChoose()) && user.getCandidate() != null) {
            addSectionHeader(contentLayout, "Professional Details");
            com.example.Candidate c = new com.example.Candidate((org.json.JSONObject) user.getCandidate());

            if (c.getCoverPic() != null && !c.getCoverPic().isEmpty()) {
                ImageView coverImage = new ImageView(new Image(c.getCoverPic(), true));
                coverImage.setFitWidth(500); // Adjusted for better fit
                coverImage.setPreserveRatio(true);
                coverImage.getStyleClass().add("cover-image");
                contentLayout.getChildren().add(2, coverImage); // Add cover below header
            }

            detailsGrid.add(createLabel("Full Name:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getFullName()), 1, rowIndex++);

            detailsGrid.add(createLabel("Location:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getLocation()), 1, rowIndex++);

            detailsGrid.add(createLabel("Phone:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getPhoneNumber()), 1, rowIndex++);

            detailsGrid.add(createLabel("Education:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getEducationalQualifications()), 1, rowIndex++);

            detailsGrid.add(createLabel("Experience:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getYearsOfExperience() + " years"), 1, rowIndex++);

            detailsGrid.add(createLabel("Preferred Position:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getPreferedPossion()), 1, rowIndex++);

            detailsGrid.add(createLabel("Skills:"), 0, rowIndex);
            Label skills = new Label(c.getSkills());
            skills.setWrapText(true);
            detailsGrid.add(skills, 1, rowIndex++);

            detailsGrid.add(createLabel("Past Experience:"), 0, rowIndex);
            Label pastExp = new Label(c.getPastExperience());
            pastExp.setWrapText(true);
            detailsGrid.add(pastExp, 1, rowIndex++);

            detailsGrid.add(createLabel("Portfolio:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getPortfolioLinks()), 1, rowIndex++);

            detailsGrid.add(createLabel("CV Link:"), 0, rowIndex);
            detailsGrid.add(new Label(c.getCv()), 1, rowIndex++);

            addAboutSection(contentLayout, "Bio", c.getBio());
            addAboutSection(contentLayout, "About", c.getAbout());

        } else if ("recruter".equalsIgnoreCase(user.getChoose()) && user.getRecruter() != null) {
            addSectionHeader(contentLayout, "Company Details");
            com.example.Recruiter r = new com.example.Recruiter((org.json.JSONObject) user.getRecruter());

            if (r.getCoverPhoto() != null && !r.getCoverPhoto().isEmpty()) {
                ImageView coverImage = new ImageView(new Image(r.getCoverPhoto(), true));
                coverImage.setFitWidth(500); // Adjusted for better fit
                coverImage.setPreserveRatio(true);
                coverImage.getStyleClass().add("cover-image");
                contentLayout.getChildren().add(2, coverImage); // Add cover below header
            }

            detailsGrid.add(createLabel("Contact Name:"), 0, rowIndex);
            detailsGrid.add(new Label(r.getName()), 1, rowIndex++);

            detailsGrid.add(createLabel("Contact Phone:"), 0, rowIndex);
            detailsGrid.add(new Label(r.getPhoneNumber()), 1, rowIndex++);

            detailsGrid.add(createLabel("Company:"), 0, rowIndex);
            detailsGrid.add(new Label(r.getCompanyName()), 1, rowIndex++);

            detailsGrid.add(createLabel("Industry:"), 0, rowIndex);
            detailsGrid.add(new Label(r.getIndustryType()), 1, rowIndex++);

            detailsGrid.add(createLabel("Office Location:"), 0, rowIndex);
            detailsGrid.add(new Label(r.getOfficeLocation() != null ? r.getOfficeLocation() : "N/A"), 1, rowIndex++);

            addAboutSection(contentLayout, "Company Description", r.getCompanyDiscription());
            addAboutSection(contentLayout, "Recruiter Bio", r.getBio());

            Button addRecruitmentButton = new Button("➕ Add Recruitment");
            addRecruitmentButton.setOnAction(e ->
                    primaryStage.setScene(new com.example.AddRecruitmentPage().createScene(primaryStage)));
            addRecruitmentButton.getStyleClass().add("action-button");
            contentLayout.getChildren().add(addRecruitmentButton);
        }

        contentLayout.getChildren().add(detailsGrid);
        scrollPane.setContent(contentLayout);
        root.setCenter(scrollPane);

        // --- Footer / Back Button ---
        Button backButton = new Button("⬅ Back to Home");
        backButton.setOnAction(e -> primaryStage.setScene(new com.example.HomePage().createScene(primaryStage)));
        HBox footer = new HBox(backButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 20, 10, 20));
        footer.getStyleClass().add("footer");
        root.setBottom(footer);

        // --- Scene Setup ---
        Scene scene = new Scene(root, 650, 800);

        // Load stylesheet safely from the 'css' sub-directory
        java.net.URL stylesheetURL = getClass().getResource("css/user-profile.css");
        if (stylesheetURL != null) {
            scene.getStylesheets().add(stylesheetURL.toExternalForm());
        } else {
            System.err.println("Error: Stylesheet 'css/user-profile.css' not found. Make sure it's in the correct resources folder.");
        }

        return scene;
    }

    // Helper method to create styled labels for the grid
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("grid-label");
        return label;
    }

    // Helper method to add section headers
    private void addSectionHeader(VBox layout, String title) {
        Label headerLabel = new Label(title);
        headerLabel.getStyleClass().add("section-header");
        VBox headerBox = new VBox(headerLabel, new Separator());
        headerBox.setSpacing(5);
        layout.getChildren().add(headerBox);
    }

    // Helper method for long text sections
    private void addAboutSection(VBox layout, String title, String content) {
        if (content != null && !content.isEmpty()) {
            VBox aboutBox = new VBox(5);
            Label aboutTitle = createLabel(title + ":");
            Label aboutContent = new Label(content);
            aboutContent.setWrapText(true);
            aboutContent.setMaxWidth(500); // Ensure text wraps
            aboutBox.getChildren().addAll(aboutTitle, aboutContent);
            layout.getChildren().add(aboutBox);
        }
    }
}