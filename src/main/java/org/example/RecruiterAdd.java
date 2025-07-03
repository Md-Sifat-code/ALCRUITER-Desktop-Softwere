package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.function.BiConsumer;

public class RecruiterAdd {

    private File coverPhotoFile = null;

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ RecruiterAdd Page Loaded");

        // UI Fields with better design
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        nameField.setStyle("-fx-padding: 10px; -fx-background-color: #f7f7f7; -fx-border-radius: 5px;");

        Button imageButton = new Button("Choose Cover Photo");
        imageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px;");

        Label imageLabel = new Label("No file selected");
        imageLabel.setStyle("-fx-text-fill: #555555;");

        imageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            coverPhotoFile = fileChooser.showOpenDialog(primaryStage);
            if (coverPhotoFile != null) {
                imageLabel.setText("Selected: " + coverPhotoFile.getName());
            }
        });

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");
        companyNameField.setStyle("-fx-padding: 10px; -fx-background-color: #f7f7f7; -fx-border-radius: 5px;");

        TextArea companyDescField = new TextArea();
        companyDescField.setPromptText("Company Description");
        companyDescField.setPrefRowCount(3);
        companyDescField.setStyle("-fx-padding: 10px; -fx-background-color: #f7f7f7; -fx-border-radius: 5px;");

        TextField industryTypeField = new TextField();
        industryTypeField.setPromptText("Industry Type");
        industryTypeField.setStyle("-fx-padding: 10px; -fx-background-color: #f7f7f7; -fx-border-radius: 5px;");

        TextArea bioField = new TextArea();
        bioField.setPromptText("Bio");
        bioField.setPrefRowCount(3);
        bioField.setStyle("-fx-padding: 10px; -fx-background-color: #f7f7f7; -fx-border-radius: 5px;");

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone Number");
        phoneNumberField.setStyle("-fx-padding: 10px; -fx-background-color: #f7f7f7; -fx-border-radius: 5px;");

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");

        submitButton.setOnAction(event -> {
            com.example.User user = com.example.UserSessionManager.getUser();
            if (user == null) {
                System.out.println("⚠️ No user found.");
                return;
            }

            try {
                String boundary = "Boundary-" + System.currentTimeMillis();
                var byteArrayOutputStream = new java.io.ByteArrayOutputStream();
                var writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(byteArrayOutputStream, java.nio.charset.StandardCharsets.UTF_8), true);

                // Helper method to add form fields
                BiConsumer<String, String> addFormField = (name, value) -> {
                    writer.append("--").append(boundary).append("\r\n");
                    writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"\r\n\r\n");
                    writer.append(value).append("\r\n");
                };

                // Add form fields
                addFormField.accept("name", nameField.getText());
                addFormField.accept("companyName", companyNameField.getText());
                addFormField.accept("companyDiscription", companyDescField.getText());
                addFormField.accept("industryType", industryTypeField.getText());
                addFormField.accept("bio", bioField.getText());
                addFormField.accept("phoneNumber", phoneNumberField.getText());
                addFormField.accept("userId", String.valueOf(user.getId()));

                // Add file if present
                if (coverPhotoFile != null) {
                    writer.append("--").append(boundary).append("\r\n");
                    writer.append("Content-Disposition: form-data; name=\"coverPhoto\"; filename=\"").append(coverPhotoFile.getName()).append("\"\r\n");
                    writer.append("Content-Type: ").append(Files.probeContentType(coverPhotoFile.toPath())).append("\r\n\r\n");
                    writer.flush();
                    Files.copy(coverPhotoFile.toPath(), byteArrayOutputStream);
                    byteArrayOutputStream.write("\r\n".getBytes());
                }

                writer.append("--").append(boundary).append("--\r\n");
                writer.flush();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://chakrihub-0qv1.onrender.com/api/recruiters/add"))
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray()))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    System.out.println("✅ Recruiter profile added successfully.");
                    System.out.println("📥 Response: " + response.body());

                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Recruiter profile created!");
                    success.showAndWait();

                    // ✅ Navigate to HomePage
                    Platform.runLater(() -> {
                        Scene homeScene = new com.example.HomePage().createScene(primaryStage);
                        primaryStage.setScene(homeScene);
                    });
                } else {
                    System.out.println("❌ Failed to add recruiter. Status: " + response.statusCode());
                    System.out.println(response.body());
                }

            } catch (Exception e) {
                System.out.println("⚠️ Error submitting form: " + e.getMessage());
            }
        });

        // Main layout with professional padding and alignment
        VBox layout = new VBox(15,
                new Label("Recruiter Profile"),
                nameField,
                new Label("Company Name:"), companyNameField,
                new Label("Company Description:"), companyDescField,
                new Label("Industry Type:"), industryTypeField,
                new Label("Bio:"), bioField,
                new Label("Phone Number:"), phoneNumberField,
                imageButton, imageLabel,
                submitButton
        );

        // Styling
        layout.setPadding(new Insets(40, 20, 20, 20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ffffff;");

        // Maximize the scene to full-screen
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setMaximized(true);  // Make it full screen
        return scene;
    }
}
