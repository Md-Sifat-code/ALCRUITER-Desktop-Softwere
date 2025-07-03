package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URL;

public class ChatWith {

    private Socket socket;
    private int senderId, receiverId;
    private VBox messagesBox;
    private Stage stage;
    private String receiverUsername;

    public Scene createScene(Stage stage, String receiverUsername) {
        this.stage = stage;
        this.receiverUsername = receiverUsername;

        com.example.UserSessionManager.fetchUserData();
        com.example.User user = com.example.UserSessionManager.getUser();
        if (user == null) return new com.example.HomePage().createScene(stage);
        senderId = user.getId();

        fetchReceiverInfoAndHistory(user.getId());

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getStyleClass().add("chat-root");

        Button back = new Button("⬅ Back");
        back.getStyleClass().add("back-button");
        back.setOnAction(e -> stage.setScene(new com.example.MatchCandidatesPage().createScene(stage, 0)));

        Label title = new Label("Chat with: @" + receiverUsername);
        title.getStyleClass().add("username-label");

        HBox topBar = new HBox(10, back, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");

        messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);

        TextField input = new TextField();
        input.setPromptText("Type message...");
        input.setPrefWidth(550);

        Button send = new Button("Send");
        send.getStyleClass().add("send-button");
        send.setOnAction(e -> sendMessage(input));

        HBox inputArea = new HBox(10, input, send);
        inputArea.setPadding(new Insets(10));
        inputArea.setAlignment(Pos.CENTER);
        inputArea.getStyleClass().add("input-area");

        root.getChildren().addAll(topBar, scrollPane, inputArea);

        Scene scene = new Scene(root, 700, 600);
        URL cssURL = getClass().getResource("css/chat_with.css");
        if (cssURL != null) scene.getStylesheets().add(cssURL.toExternalForm());

        return scene;
    }

    private void fetchReceiverInfoAndHistory(int senderId) {
        new Thread(() -> {
            try {
                // Receiver info
                HttpRequest rReq = HttpRequest.newBuilder()
                        .uri(URI.create("https://chakrihub-0qv1.onrender.com/User/search/" + receiverUsername))
                        .GET().build();
                JSONObject rObj = new JSONObject(HttpClient.newHttpClient()
                        .send(rReq, HttpResponse.BodyHandlers.ofString()).body());
                receiverId = rObj.getInt("id");

                // Fetch history
                HttpRequest hReq = HttpRequest.newBuilder()
                        .uri(URI.create("https://chakrihub-0qv1.onrender.com/messages/" + senderId + "/" + receiverId))
                        .GET().build();
                HttpResponse<String> resp = HttpClient.newHttpClient()
                        .send(hReq, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200) {
                    JSONArray arr = new JSONArray(resp.body());
                    Platform.runLater(() -> {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject m = arr.getJSONObject(i);
                            boolean isSender = m.getInt("senderId") == senderId;
                            String content = m.getString("content");
                            addMessageBubble(content, isSender);
                        }
                    });
                }

                setupSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupSocket() {
        try {
            socket = IO.socket("https://chakrihub-0qv1.onrender.com");
            socket.on(Socket.EVENT_CONNECT, args -> System.out.println("Socket connected"));
            socket.on("chat", args -> {
                JSONObject m = (JSONObject) args[0];
                int from = m.getInt("senderId");
                String txt = m.getString("content");
                Platform.runLater(() -> addMessageBubble(txt, from == senderId));
            });
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(TextField input) {
        String txt = input.getText().trim();
        if (txt.isEmpty() || socket == null) return;

        JSONObject msg = new JSONObject()
                .put("senderId", senderId)
                .put("receiverId", receiverId)
                .put("content", txt);

        socket.emit("chat", msg);
        addMessageBubble(txt, true);
        input.clear();
    }

    private void addMessageBubble(String text, boolean isSender) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.getStyleClass().add(isSender ? "user-message" : "bot-message");
        HBox h = new HBox(lbl);
        h.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        h.setPadding(new Insets(5));
        messagesBox.getChildren().add(h);
    }
}
