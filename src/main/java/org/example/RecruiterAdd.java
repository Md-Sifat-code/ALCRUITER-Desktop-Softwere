package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RecruiterAdd {

    public Scene createScene(Stage primaryStage) {
        System.out.println("✅ RecruiterAdd Page Loaded");

        Label titleLabel = new Label("Recruiter Registration");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> primaryStage.setScene(new com.example.RolechoosePage().createScene(primaryStage)));

        VBox layout = new VBox(20, titleLabel, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        return new Scene(layout, 400, 300);
    }
}
