package com.example;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginPage {

    public Scene createScene(Stage primaryStage) {
        System.out.println("Login Page Loaded");

        // Title Label
        Label titleLabel = new Label("AL CRUITER");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        // Username Field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(300);

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(300);

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white;");
        loginButton.setMaxWidth(300);

        // Handle Login Click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            System.out.println("Login Button Clicked");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Username or Password is empty.");
                showAlert(Alert.AlertType.WARNING, "Login Failed", "Username and password cannot be empty.");
                return;
            }

            makePostRequest(username, password, primaryStage);
        });

        // Layout
        VBox vbox = new VBox(15, titleLabel, usernameField, passwordField, loginButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 30px;");
        vbox.setPrefWidth(400);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #4B8DF8;");
        root.getChildren().add(vbox);

        return new Scene(root, 600, 400);
    }

    // Handle Login Request
    private void makePostRequest(String username, String password, Stage primaryStage) {
        System.out.println("Starting Login Request...");

        String apiUrl = "https://chakrihub-mhh5.onrender.com/Log";

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("username", username);
        jsonPayload.put("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString(), StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("Response Received. Status Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());

                    if (response.statusCode() == 200) {
                        JSONObject responseJson = new JSONObject(response.body());
                        String token = responseJson.getString("token");
                        String usernameResponse = responseJson.getString("username");
                        String email = responseJson.getString("email");

                        // Convert roles from JSONArray to String[]
                        JSONArray rolesArray = responseJson.getJSONArray("roles");
                        String[] roles = new String[rolesArray.length()];
                        for (int i = 0; i < rolesArray.length(); i++) {
                            roles[i] = rolesArray.getString(i);
                        }

                        System.out.println("Login Successful. Saving Session...");
                        System.out.println("Token: " + token);
                        System.out.println("Username: " + usernameResponse);
                        System.out.println("Email: " + email);
                        System.out.println("Roles: " + String.join(", ", roles));

                        // Save session
                        com.example.SessionManager.setSession(token, usernameResponse, email, roles);

                        // Show success message & navigate to HomePage
                        Platform.runLater(() -> showLoginSuccessDialog(primaryStage));
                    } else {
                        System.out.println("Login Failed. Invalid credentials.");
                        Platform.runLater(this::showLoginFailureDialog);
                    }
                })
                .exceptionally(e -> {
                    System.out.println("Request Failed: " + e.getMessage());
                    Platform.runLater(this::showLoginFailureDialog);
                    return null;
                });
    }

    // Show Login Success Dialog
    private void showLoginSuccessDialog(Stage primaryStage) {
        System.out.println("Displaying Success Dialog...");
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Login Successful");
        successAlert.setHeaderText("You have successfully logged in!");
        successAlert.setContentText("Redirecting to your dashboard...");

        successAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Redirecting to HomePage...");
                navigateToHome(primaryStage);
            }
        });
    }

    // Show Login Failure Dialog
    private void showLoginFailureDialog() {
        System.out.println("Displaying Failure Dialog...");
        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Username or Password!");
    }

    // Generic Alert Method
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigate to HomePage
    private void navigateToHome(Stage primaryStage) {
        System.out.println("Navigating to HomePage...");
        com.example.HomePage homePage = new com.example.HomePage();
        Scene homeScene = homePage.createScene(primaryStage);
        primaryStage.setScene(homeScene);
    }
}
