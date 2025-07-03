package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

        // Main layout container
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: #f4f7f6;"); // Light background

        // Title
        Label titleLabel = new Label("➕ Add New Recruitment");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        VBox.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // Form fields using GridPane for better alignment
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Body Label and TextArea
        Label bodyLabel = new Label("Recruitment Description:");
        bodyLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Enter a detailed recruitment description...");
        bodyField.setWrapText(true);
        bodyField.setPrefRowCount(8); // Increased rows for more input
        bodyField.setStyle("-fx-control-inner-background: #ecf0f1; -fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");

        // File selection
        Label fileLabel = new Label("No cover photo selected.");
        fileLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        Button chooseFileBtn = new Button("📁 Choose Cover Photo");
        chooseFileBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        FileChooser fileChooser = new FileChooser();
        final File[] selectedFile = {null};

        chooseFileBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText("Selected: " + file.getName());
                System.out.println("📷 File selected: " + file.getAbsolutePath());
            } else {
                fileLabel.setText("No cover photo selected.");
                selectedFile[0] = null;
                System.out.println("📷 File selection cancelled.");
            }
        });

        // Add elements to the grid
        formGrid.add(bodyLabel, 0, 0);
        formGrid.add(bodyField, 0, 1);
        GridPane.setColumnSpan(bodyField, 2); // Span across two columns
        formGrid.add(new Label("Cover Photo:"), 0, 2); // Label for file selection
        formGrid.add(chooseFileBtn, 0, 3);
        formGrid.add(fileLabel, 1, 3);


        // Buttons
        Button submitButton = new Button("✅ Submit Recruitment");
        submitButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        submitButton.setOnMouseEntered(e -> submitButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px; -fx-cursor: hand;"));
        submitButton.setOnMouseExited(e -> submitButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px;"));

        Button cancelButton = new Button("❌ Cancel");
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px; -fx-cursor: hand;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px;"));

        Button backButton = new Button("⬅️ Back to Home");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px; -fx-cursor: hand;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 25px; -fx-border-radius: 5px;"));


        HBox buttonBox = new HBox(20, submitButton, cancelButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBox, new Insets(20, 0, 0, 0));


        submitButton.setOnAction(e -> {
            String bodyText = bodyField.getText().trim();
            int userId = com.example.UserSessionManager.getUser().getId();

            System.out.println("🧾 Preparing to submit recruitment:");
            System.out.println("Body: " + bodyText);
            System.out.println("User ID: " + userId);
            System.out.println("File: " + (selectedFile[0] != null ? selectedFile[0].getAbsolutePath() : "None"));

            if (bodyText.isEmpty()) {
                showAlert("Please provide a recruitment description.");
                System.out.println("⚠️ Submission blocked: missing body.");
                return;
            }
            if (selectedFile[0] == null) {
                showAlert("Please select a cover photo for the recruitment.");
                System.out.println("⚠️ Submission blocked: missing cover photo.");
                return;
            }


            try {
                String url = "https://chakrihub-0qv1.onrender.com/Post/add";
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

        backButton.setOnAction(e -> {
            System.out.println("⬅️ Back to Home clicked.");
            primaryStage.setScene(new com.example.HomePage().createScene(primaryStage));
        });

        mainLayout.getChildren().addAll(titleLabel, formGrid, buttonBox);

        // Make the formGrid take up available width
        HBox.setHgrow(formGrid, Priority.ALWAYS);
        VBox.setVgrow(formGrid, Priority.ALWAYS);


        return new Scene(mainLayout, 800, 700); // Increased scene size for better layout
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null); // No header for a cleaner look
        alert.setTitle("Recruitment Submission");
        alert.showAndWait();
    }
}