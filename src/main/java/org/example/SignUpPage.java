package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

public class SignUpPage {

    public Scene createScene(Stage primaryStage) {
        // Title Section (AL CRUITER)
        Label titleLabel = new Label("AL CRUITER");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        // Username Field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 5px;");
        usernameField.setMaxWidth(300);

        // Email Field
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 5px;");
        emailField.setMaxWidth(300);

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 5px;");
        passwordField.setMaxWidth(300);

        // Confirm Password Field
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 5px;");
        confirmPasswordField.setMaxWidth(300);

        // Picture Upload Button
        Button uploadButton = new Button("Upload Picture");
        uploadButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        uploadButton.setMaxWidth(300);

        // File chooser to select picture
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

        // Handle file selection
        final File[] selectedFile = new File[1];
        uploadButton.setOnAction(e -> {
            selectedFile[0] = fileChooser.showOpenDialog(primaryStage);
        });

        // Sign Up Button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        signUpButton.setMaxWidth(300);

        // Error Message Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        // Handle Sign Up Action
        signUpButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Basic validation for sign up
            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match!");
            } else if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields!");
            } else if (selectedFile[0] == null) {
                errorLabel.setText("Please upload a profile picture!");
            } else {
                try {
                    // Proceed with sending the POST request (multipart/form-data)
                    sendPostRequest(username, email, password, selectedFile[0], errorLabel, primaryStage);
                } catch (IOException ioException) {
                    errorLabel.setText("Error during sign-up!");
                }
            }
        });

        // Hyperlink for Login
        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: #1E3A8A; -fx-font-size: 14px;");
        loginLink.setOnAction(e -> {
            // Go back to LoginPage when clicked
            com.example.LoginPage loginPage = new com.example.LoginPage();
            Scene loginScene = loginPage.createScene(primaryStage); // Create LoginPage scene
            primaryStage.setScene(loginScene); // Set the LoginPage scene
        });

        // VBox Layout for Centering the Components
        VBox vbox = new VBox(15, titleLabel, usernameField, emailField, passwordField, confirmPasswordField, uploadButton, signUpButton, errorLabel, loginLink);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 30px; -fx-border-radius: 10px;");
        vbox.setPrefWidth(400);

        // Outer Background and Content Pane
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #4B8DF8;");
        root.getChildren().add(vbox);

        // Scene Setup
        Scene scene = new Scene(root, 600, 500);
        return scene;
    }

    private void sendPostRequest(String username, String email, String password, File file, Label errorLabel, Stage primaryStage) throws IOException {
        String apiUrl = "https://chakrihub-r7m5.onrender.com/User/add";

        // Create boundary for multipart request
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Accept", "application/json");

        // Output stream for writing form data
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

        // Add username, email, and password to form data
        writeFormField(outputStream, boundary, "username", username);
        writeFormField(outputStream, boundary, "email", email);
        writeFormField(outputStream, boundary, "password", password);

        // Add the picture file to the form data as "profilpic"
        writeFileField(outputStream, boundary, "profilpic", file);

        // End the form data
        outputStream.writeBytes("--" + boundary + "--\r\n");
        outputStream.flush();

        // Get the server response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            errorLabel.setText("Sign Up Successful!");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

            // After successful sign-up, route the user to the LoginPage
            // Create the LoginPage scene
            com.example.LoginPage loginPage = new com.example.LoginPage();
            Scene loginScene = loginPage.createScene(primaryStage); // Create LoginPage scene
            primaryStage.setScene(loginScene); // Set the LoginPage scene
        } else {
            errorLabel.setText("Error during sign-up!");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
        }
    }


    private void writeFormField(DataOutputStream outputStream, String boundary, String fieldName, String fieldValue) throws IOException {
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n");
        outputStream.writeBytes("\r\n");
        outputStream.writeBytes(fieldValue + "\r\n");
    }

    private void writeFileField(DataOutputStream outputStream, String boundary, String fieldName, File file) throws IOException {
        String fileName = file.getName();
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"\r\n");
        outputStream.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n");
        outputStream.writeBytes("\r\n");

        // Write the file content
        Files.copy(file.toPath(), outputStream);

        outputStream.writeBytes("\r\n");
    }
}
