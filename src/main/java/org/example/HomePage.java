package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage {

    public Scene createScene(Stage primaryStage) {
        // Get user data from SessionManager
        String username = com.example.SessionManager.getUsername();
        String email = com.example.SessionManager.getEmail();
        String[] roles = com.example.SessionManager.getRoles();
        String rolesText = roles != null ? String.join(", ", roles) : "No roles assigned";

        // Labels
        Label welcomeLabel = new Label("Welcome, " + (username != null ? username : "User") + "!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label emailLabel = new Label("Email: " + (email != null ? email : "N/A"));
        Label rolesLabel = new Label("Roles: " + rolesText);

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            com.example.SessionManager.clearSession();
            navigateToLogin(primaryStage);
        });

        VBox vbox = new VBox(15, welcomeLabel, emailLabel, rolesLabel, logoutButton);
        vbox.setAlignment(Pos.CENTER);

        return new Scene(new StackPane(vbox), 600, 400);
    }

    private void navigateToLogin(Stage primaryStage) {
        Scene loginScene = new com.example.LoginPage().createScene(primaryStage);
        primaryStage.setScene(loginScene);
    }
}
