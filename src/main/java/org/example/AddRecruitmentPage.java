package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;

public class AddRecruitmentPage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("📄 Opening AddRecruitmentPage...");

        Label titleLabel = new Label("➕ Add Recruitment");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Enter recruitment description...");
        bodyField.setWrapText(true);
        bodyField.setPrefRowCount(5);

        Label fileLabel = new Label("No image selected.");
        Button chooseFileBtn = new Button("📁 Choose Cover Photo");
        FileChooser fileChooser = new FileChooser();
        final File[] selectedFile = {null};

        chooseFileBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText("Selected: " + file.getName());
                System.out.println("📷 File selected: " + file.getAbsolutePath());
            }
        });

        Button submitButton = new Button("✅ Submit");
        Button cancelButton = new Button("❌ Cancel");

        submitButton.setOnAction(e -> {
            String bodyText = bodyField.getText().trim();
            int userId = com.example.UserSessionManager.getUser().getId();

            System.out.println("🧾 Preparing to submit recruitment:");
            System.out.println("Body: " + bodyText);
            System.out.println("User ID: " + userId);
            System.out.println("File: " + (selectedFile[0] != null ? selectedFile[0].getAbsolutePath() : "None"));

            if (bodyText.isEmpty() || selectedFile[0] == null) {
                showAlert("All fields are required.");
                System.out.println("⚠️ Submission blocked: missing body or cover photo.");
                return;
            }

            try {
                String url = "https://chakrihub-1-cilx.onrender.com/Post/add";
                System.out.println("🌐 Sending POST request to: " + url);

                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = new HttpPost(url);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("body", bodyText, ContentType.TEXT_PLAIN);
                builder.addTextBody("userId", String.valueOf(userId), ContentType.TEXT_PLAIN);
                builder.addBinaryBody("coverPhoto", selectedFile[0], ContentType.DEFAULT_BINARY, selectedFile[0].getName());

                HttpEntity entity = builder.build();
                post.setEntity(entity);

                HttpResponse response = client.execute(post);
                client.close();

                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("📬 Server Response: HTTP " + statusCode);

                if (statusCode == 200 || statusCode == 201) {
                    showAlert("Recruitment added successfully.");
                    primaryStage.setScene(new com.example.UserProfilePage().createScene(primaryStage));
                } else {
                    showAlert("Failed to submit recruitment. Server responded with status: " + statusCode);
                }

            } catch (IOException ex) {
                String errorMsg = "❌ Error during submission: " + ex.getMessage();
                showAlert(errorMsg);
                System.out.println(errorMsg);
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> {
            System.out.println("↩️ Cancel clicked. Returning to profile page.");
            primaryStage.setScene(new com.example.UserProfilePage().createScene(primaryStage));
        });

        VBox layout = new VBox(15, titleLabel, bodyField, chooseFileBtn, fileLabel, submitButton, cancelButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 600, 500);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
