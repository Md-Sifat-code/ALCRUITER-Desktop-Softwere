package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create an instance of LoginPage
        com.example.LoginPage loginPage = new com.example.LoginPage();

        // Pass the primaryStage to LoginPage's createScene method
        Scene scene = loginPage.createScene(primaryStage); // Pass Stage

        // Set up the primary stage and scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
