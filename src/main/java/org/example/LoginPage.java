package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class LoginPage {

    public Scene createScene(Stage primaryStage) {
        // Title Section (AL CRUITER)
        Label titleLabel = new Label("AL CRUITER");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        // Username Field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 5px;");
        usernameField.setMaxWidth(300); // Set the width to match the input section

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 5px;");
        passwordField.setMaxWidth(300); // Set the width to match the input section

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        loginButton.setMaxWidth(300); // Set the button width to match input fields
        loginButton.setMinWidth(300); // Ensure button is not smaller than input fields

        // Error Message Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        // Handle Login Action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Make the POST request to the API
            makePostRequest(username, password, errorLabel, primaryStage);
        });

        // Label for "New to AL CRUITER? Sign up" text
        Label signUpLabel = new Label("New to AL CRUITER? ");
        signUpLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

        // Hyperlink for "Sign Up"
        Hyperlink signUpLink = new Hyperlink("Sign up");
        signUpLink.setStyle("-fx-text-fill: #1E3A8A; -fx-font-size: 14px;");
        signUpLink.setOnAction(e -> {
            // Open SignUpPage when clicked
            com.example.SignUpPage signUpPage = new com.example.SignUpPage();
            Scene signUpScene = signUpPage.createScene(primaryStage); // Create SignUpPage scene
            primaryStage.setScene(signUpScene); // Set the SignUpPage scene
        });

        // HBox Layout for the SignUp label and link
        HBox signUpBox = new HBox(5, signUpLabel, signUpLink);
        signUpBox.setAlignment(Pos.CENTER);

        // VBox Layout for Centering the Components
        VBox vbox = new VBox(15, titleLabel, usernameField, passwordField, loginButton, errorLabel, signUpBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 30px; -fx-border-radius: 10px;");
        vbox.setPrefWidth(400);

        // Outer Background and Content Pane
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #4B8DF8;");
        root.getChildren().add(vbox);

        // Scene Setup
        Scene scene = new Scene(root, 600, 400);
        return scene;
    }

    private void makePostRequest(String username, String password, Label errorLabel, Stage primaryStage) {
        // API URL
        String apiUrl = "https://chakrihub-1.onrender.com/Log";

        // Create JSON payload
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("username", username);
        jsonPayload.put("password", password);

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString(), StandardCharsets.UTF_8))
                .build();

        // Send the request and handle the response
        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // Handle the API response
                    if (response.statusCode() == 200) {
                        // Successful login
                        errorLabel.setText("Login Successful!");
                        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
                        // Proceed to the next page or app logic
                    } else {
                        // Invalid login
                        errorLabel.setText("Invalid Username or Password!");
                        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
                    }
                })
                .exceptionally(e -> {
                    // Handle error if there is an issue with the HTTP request
                    errorLabel.setText("Error connecting to the server!");
                    errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
                    return null;
                });
    }
}
