package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserProfilePage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ UserProfilePage Loaded");

        com.example.User user = com.example.UserSessionManager.getUser();
        if (user == null) {
            System.out.println("⚠️ No user found. Redirecting to HomePage.");
            return new com.example.HomePage().createScene(primaryStage);
        }

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");

        // Title
        Label titleLabel = new Label("👤 User Profile");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        layout.getChildren().add(titleLabel);

        // Profile Picture
        if (user.getProfilpic() != null && !user.getProfilpic().isEmpty()) {
            ImageView profileImageView = new ImageView(new Image(user.getProfilpic(), true));
            profileImageView.setFitWidth(100);
            profileImageView.setFitHeight(100);
            layout.getChildren().add(profileImageView);
        }

        // Basic Info
        layout.getChildren().addAll(
                new Label("Username: " + user.getUsername()),
                new Label("Email: " + user.getEmail()),
                new Label("Role: " + user.getChoose())
        );

        layout.getChildren().add(new Separator());

        // Candidate Info
        if ("candidate".equalsIgnoreCase(user.getChoose()) && user.getCandidate() != null) {
            com.example.Candidate c = new com.example.Candidate((org.json.JSONObject) user.getCandidate());

            if (c.getCoverPic() != null && !c.getCoverPic().isEmpty()) {
                ImageView coverImage = new ImageView(new Image(c.getCoverPic(), true));
                coverImage.setFitWidth(400);
                coverImage.setFitHeight(150);
                layout.getChildren().add(coverImage);
            }

            layout.getChildren().addAll(
                    new Label("Full Name: " + c.getFullName()),
                    new Label("Location: " + c.getLocation()),
                    new Label("Phone Number: " + c.getPhoneNumber()),
                    new Label("Education: " + c.getEducationalQualifications()),
                    new Label("Years of Experience: " + c.getYearsOfExperience()),
                    new Label("Preferred Position: " + c.getPreferedPossion()),
                    new Label("Skills: " + c.getSkills()),
                    new Label("Past Experience: " + c.getPastExperience()),
                    new Label("Portfolio: " + c.getPortfolioLinks()),
                    new Label("CV Link: " + c.getCv()),
                    new Label("Bio: " + c.getBio()),
                    new Label("About: " + c.getAbout())
            );
        }

        // Recruiter Info
        else if ("recruter".equalsIgnoreCase(user.getChoose()) && user.getRecruter() != null) {
            com.example.Recruiter r = new com.example.Recruiter((org.json.JSONObject) user.getRecruter());

            if (r.getCoverPhoto() != null && !r.getCoverPhoto().isEmpty()) {
                ImageView coverImage = new ImageView(new Image(r.getCoverPhoto(), true));
                coverImage.setFitWidth(400);
                coverImage.setFitHeight(150);
                layout.getChildren().add(coverImage);
            }

            layout.getChildren().addAll(
                    new Label("Name: " + r.getName()),
                    new Label("Phone Number: " + r.getPhoneNumber()),
                    new Label("Company: " + r.getCompanyName()),
                    new Label("Industry Type: " + r.getIndustryType()),
                    new Label("Office Location: " + (r.getOfficeLocation() != null ? r.getOfficeLocation() : "N/A")),
                    new Label("Company Description: " + r.getCompanyDiscription()),
                    new Label("Bio: " + r.getBio())
            );

            // Add Recruitment Button
            Button addRecruitmentButton = new Button("➕ Add Recruitment");
            addRecruitmentButton.setOnAction(e ->
                    primaryStage.setScene(new com.example.AddRecruitmentPage().createScene(primaryStage)));
            layout.getChildren().add(addRecruitmentButton);
        }

        layout.getChildren().add(new Separator());

        // Back button
        Button backButton = new Button("⬅ Back to Home");
        backButton.setOnAction(e -> primaryStage.setScene(new com.example.HomePage().createScene(primaryStage)));
        layout.getChildren().add(backButton);

        return new Scene(layout, 600, 800);
    }
}
