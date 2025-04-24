package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class CandidateAdd {

    public Scene createScene(Stage primaryStage) {
        Label titleLabel = new Label("Candidate Submission");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        // Text fields for user input
        TextField fullNameField = createTextField("Full Name");
        TextField phoneNumberField = createTextField("Phone Number");
        TextField locationField = createTextField("Location");
        TextArea bioField = createTextArea("Bio");
        TextArea aboutField = createTextArea("About");
        TextArea skillsField = createTextArea("Skills");
        TextArea languageField = createTextArea("Languages");
        TextField portfolioLinksField = createTextField("Portfolio Links");
        TextField preferredPositionField = createTextField("Preferred Position");
        TextField yearsOfExperienceField = createTextField("Years of Experience");
        TextArea educationalQualificationsField = createTextArea("Educational Qualifications");
        TextArea pastExperienceField = createTextArea("Past Experience");

        // File upload buttons
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

        Label coverPicLabel = new Label("No file selected");
        Button uploadCoverPicButton = new Button("Upload Cover Picture");
        final File[] coverPic = new File[1];
        uploadCoverPicButton.setOnAction(e -> {
            coverPic[0] = fileChooser.showOpenDialog(primaryStage);
            if (coverPic[0] != null) {
                coverPicLabel.setText(coverPic[0].getName());
            }
        });

        Label cvLabel = new Label("No file selected");
        Button uploadCVButton = new Button("Upload CV");
        FileChooser cvChooser = new FileChooser();
        cvChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        final File[] cvFile = new File[1];
        uploadCVButton.setOnAction(e -> {
            cvFile[0] = cvChooser.showOpenDialog(primaryStage);
            if (cvFile[0] != null) {
                cvLabel.setText(cvFile[0].getName());
            }
        });

        // Submit button
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-size: 14px;");
        Label errorLabel = new Label();

        submitButton.setOnAction(e -> {
            try {
                com.example.User user = com.example.UserSessionManager.getUser();
                if (user == null) {
                    errorLabel.setText("⚠️ User not found. Please login again.");
                    return;
                }

                int userId = user.getId(); // Get user ID
                sendPostRequest(
                        fullNameField.getText(),
                        phoneNumberField.getText(),
                        locationField.getText(),
                        bioField.getText(),
                        aboutField.getText(),
                        skillsField.getText(),
                        languageField.getText(),
                        portfolioLinksField.getText(),
                        preferredPositionField.getText(),
                        yearsOfExperienceField.getText(),
                        educationalQualificationsField.getText(),
                        pastExperienceField.getText(),
                        coverPic[0],
                        cvFile[0],
                        userId,
                        errorLabel,
                        primaryStage
                );
            } catch (IOException ioException) {
                errorLabel.setText("⚠️ Error submitting data.");
            }
        });

        // Layout setup
        VBox vbox = new VBox(10, titleLabel, fullNameField, phoneNumberField, locationField, bioField, aboutField, skillsField, languageField,
                portfolioLinksField, preferredPositionField, yearsOfExperienceField, educationalQualificationsField, pastExperienceField,
                uploadCoverPicButton, coverPicLabel, uploadCVButton, cvLabel, submitButton, errorLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px; -fx-border-radius: 10px;");

        return new Scene(vbox, 600, 800);
    }

    private void sendPostRequest(
            String fullName, String phoneNumber, String location, String bio, String about, String skills,
            String language, String portfolioLinks, String preferredPosition, String yearsOfExperience,
            String educationalQualifications, String pastExperience, File coverPic, File cvFile,
            int userId, Label errorLabel, Stage primaryStage
    ) throws IOException {

        String apiUrl = "https://chakrihub-r7m5.onrender.com/api/candidates/add";
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Accept", "application/json");

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

        // Adding form fields
        writeFormField(outputStream, boundary, "fullName", fullName);
        writeFormField(outputStream, boundary, "phoneNumber", phoneNumber);
        writeFormField(outputStream, boundary, "location", location);
        writeFormField(outputStream, boundary, "bio", bio);
        writeFormField(outputStream, boundary, "about", about);
        writeFormField(outputStream, boundary, "skills", skills);
        writeFormField(outputStream, boundary, "language", language);
        writeFormField(outputStream, boundary, "portfolioLinks", portfolioLinks);
        writeFormField(outputStream, boundary, "preferredPosition", preferredPosition);
        writeFormField(outputStream, boundary, "yearsOfExperience", String.valueOf(yearsOfExperience));
        writeFormField(outputStream, boundary, "educationalQualifications", educationalQualifications);
        writeFormField(outputStream, boundary, "pastExperience", pastExperience);
        writeFormField(outputStream, boundary, "userId", String.valueOf(userId)); // Convert int to String

        // Adding file fields
        if (coverPic != null) writeFileField(outputStream, boundary, "coverPic", coverPic);
        if (cvFile != null) writeFileField(outputStream, boundary, "cv", cvFile);

        outputStream.writeBytes("--" + boundary + "--\r\n");
        outputStream.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            errorLabel.setText("✅ Submission successful!");
            errorLabel.setStyle("-fx-text-fill: green;");
            primaryStage.setScene(new com.example.HomePage().createScene(primaryStage));
        } else {
            errorLabel.setText("❌ Error during submission!");
        }
    }

    private void writeFormField(DataOutputStream outputStream, String boundary, String fieldName, String fieldValue) throws IOException {
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n");
        outputStream.writeBytes(fieldValue + "\r\n");
    }

    private void writeFileField(DataOutputStream outputStream, String boundary, String fieldName, File file) throws IOException {
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n");
        outputStream.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n\r\n");
        Files.copy(file.toPath(), outputStream);
        outputStream.writeBytes("\r\n");
    }

    private TextField createTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setMaxWidth(300);
        return textField;
    }

    private TextArea createTextArea(String placeholder) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(placeholder);
        textArea.setMaxWidth(300);
        textArea.setWrapText(true);
        return textArea;
    }
}
