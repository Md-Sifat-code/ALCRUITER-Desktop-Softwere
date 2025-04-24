package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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

        // UI Fields
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        Button imageButton = new Button("Choose Cover Photo");
        Label imageLabel = new Label("No file selected");

        imageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Cover Photo");
            coverPhotoFile = fileChooser.showOpenDialog(primaryStage);
            if (coverPhotoFile != null) {
                imageLabel.setText("Selected: " + coverPhotoFile.getName());
            }
        });

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");

        TextArea companyDescField = new TextArea();
        companyDescField.setPromptText("Company Description");
        companyDescField.setPrefRowCount(3);

        TextField industryTypeField = new TextField();
        industryTypeField.setPromptText("Industry Type");

        TextArea bioField = new TextArea();
        bioField.setPromptText("Bio");
        bioField.setPrefRowCount(3);

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone Number");

        Button submitButton = new Button("Submit");

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
                        .uri(URI.create("https://chakrihub-r7m5.onrender.com/api/recruiters/add"))
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
                }
                else {
                    System.out.println("❌ Failed to add recruiter. Status: " + response.statusCode());
                    System.out.println(response.body());
                }

            } catch (Exception e) {
                System.out.println("⚠️ Error submitting form: " + e.getMessage());
            }
        });


        VBox layout = new VBox(10,
                new Label("Name:"), nameField,
                imageButton, imageLabel,
                new Label("Company Name:"), companyNameField,
                new Label("Company Description:"), companyDescField,
                new Label("Industry Type:"), industryTypeField,
                new Label("Bio:"), bioField,
                new Label("Phone Number:"), phoneNumberField,
                submitButton
        );

        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        return new Scene(layout, 500, 600);
    }
}
