package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class ChatClientWindow extends Application {
    private WebSocket webSocket;
    private TextArea messageArea;
    private TextField inputField;
    private Button sendButton;
    private Label statusLabel;

    private int receiverId;
    private int senderId;
    private Integer chatId = null;  // store existing chat ID
    private final String WS_URL = "wss://chakrihub-0qv1.onrender.com/ws/websocket";
    private final String MSG_HISTORY_URL = "https://chakrihub-0qv1.onrender.com/messages/%d/%d";

    public Scene createScene(Stage primaryStage, int receiverId) {
        System.out.println("createScene called with receiverId: " + receiverId);
        this.receiverId = receiverId;
        com.example.UserSessionManager.fetchUserData();
        senderId = com.example.UserSessionManager.getUser().getId();
        System.out.println("Sender ID: " + senderId);

        // UI setup
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setPrefHeight(300);
        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        sendButton = new Button("Send");
        sendButton.setDisable(true);
        sendButton.setMaxWidth(Double.MAX_VALUE);
        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        Button backBtn = new Button("⬅ Back");
        backBtn.setOnAction(e -> {
            System.out.println("Back button pressed. Navigating to JobsPage.");
            primaryStage.setScene(new com.example.JobsPage().createScene(primaryStage));
        });

        statusLabel = new Label("Status: Disconnected");
        statusLabel.setStyle("-fx-font-weight:bold; -fx-text-fill:red;");

        VBox root = new VBox(10, backBtn, statusLabel, messageArea, inputField, sendButton);
        root.setPadding(new Insets(20));

        primaryStage.setOnCloseRequest(evt -> {
            System.out.println("Primary stage closing. Closing WebSocket.");
            closeWebSocket();
        });
        connectWebSocket();
        loadChatHistory();  // fetch past messages

        return new Scene(root, 500, 500);
    }

    @Override
    public void start(Stage stage) {
        System.out.println("Application started.");
        stage.setScene(createScene(stage, 2));  // for testing
        stage.setTitle("Chat");
        stage.show();
    }

    private void loadChatHistory() {
        String url = String.format(MSG_HISTORY_URL, senderId, receiverId);
        System.out.println("Loading chat history from: " + url);
        HttpClient.newHttpClient().sendAsync(
                        HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                        HttpResponse.BodyHandlers.ofString()
                ).thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    System.out.println("Chat history received: " + body);
                    Platform.runLater(() -> {
                        var arr = new org.json.JSONArray(body);
                        for (int i = 0; i < arr.length(); i++) {
                            var o = arr.getJSONObject(i);
                            if (chatId == null && o.has("chatId")) {
                                chatId = o.getInt("chatId");
                                System.out.println("Chat ID set to: " + chatId);
                            }
                            boolean isSender = (o.getInt("senderId") == senderId);
                            String prefix = isSender ? "🗣️ You: " : "📥 Them: ";
                            String content = o.getString("content");
                            System.out.println("Loading message: " + prefix + content);
                            messageArea.appendText(prefix);
                            messageArea.appendText(content + "\n");
                        }
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("Failed to load chat history: " + ex.getMessage());
                    return null;
                });
    }

    public void connectWebSocket() {
        if (webSocket != null) {
            System.out.println("Closing existing WebSocket connection before reconnecting.");
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Reconnecting");
        }
        System.out.println("Connecting to WebSocket at " + WS_URL);
        statusLabel.setText("Status: Connecting...");
        statusLabel.setStyle("-fx-font-weight:bold;-fx-text-fill:orange;");
        sendButton.setDisable(true);

        HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new WebSocket.Listener() {
                    @Override
                    public void onOpen(WebSocket ws) {
                        System.out.println("WebSocket connection opened.");
                        webSocket = ws;
                        Platform.runLater(() -> {
                            statusLabel.setText("Connected");
                            statusLabel.setStyle("-fx-font-weight:bold;-fx-text-fill:green;");
                            sendButton.setDisable(false);
                            inputField.requestFocus();
                        });
                        ws.request(1);
                    }
                    @Override
                    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                        System.out.println("Received WebSocket message: " + data);
                        JSONObject o = new JSONObject(data.toString());
                        Platform.runLater(() -> {
                            String prefix = (o.getInt("senderId") == senderId ? "🗣️ You: " : "📥 Them: ");
                            String content = o.getString("content");
                            System.out.println("Appending message to UI: " + prefix + content);
                            messageArea.appendText(prefix + content + "\n");
                        });
                        ws.request(1);
                        return CompletableFuture.completedFuture(null);
                    }
                    @Override
                    public CompletionStage<?> onClose(WebSocket ws, int sc, String r) {
                        System.out.println("WebSocket closed. Code: " + sc + ", Reason: " + r);
                        webSocket = null;
                        Platform.runLater(() -> statusLabel.setText("Disconnected"));
                        return CompletableFuture.completedFuture(null);
                    }
                    @Override
                    public void onError(WebSocket ws, Throwable err) {
                        System.out.println("WebSocket error: " + err.getMessage());
                        webSocket = null;
                        Platform.runLater(() -> statusLabel.setText("Error: " + err.getMessage()));
                    }
                }).exceptionally(ex -> {
                    System.out.println("WebSocket connection failed: " + ex.getMessage());
                    Platform.runLater(() -> statusLabel.setText("Failed"));
                    return null;
                });
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        System.out.println("Attempting to send message: '" + text + "'");
        if (webSocket == null) {
            System.out.println("Cannot send message: WebSocket is null.");
            return;
        }
        if (text.isEmpty()) {
            System.out.println("Cannot send message: Text is empty.");
            return;
        }

        JSONObject msg = new JSONObject();
        msg.put("senderId", senderId);
        msg.put("receiverId", receiverId);
        msg.put("content", text);
        if (chatId != null) {
            msg.put("chatId", chatId);
            System.out.println("Including chatId in message: " + chatId);
        }

        webSocket.sendText(msg.toString(), true)
                .thenRun(() -> Platform.runLater(() -> {
                    System.out.println("Message sent successfully, appending to UI.");
                    messageArea.appendText("🗣️ You: " + text + "\n");
                    inputField.clear();
                }))
                .exceptionally(ex -> {
                    System.out.println("Failed to send message: " + ex.getMessage());
                    Platform.runLater(() -> messageArea.appendText("⚠️ Failed: " + ex.getMessage() + "\n"));
                    return null;
                });
    }

    private void closeWebSocket() {
        if (webSocket != null) {
            System.out.println("Closing WebSocket connection.");
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Client closing");
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException ignored) {}
        } else {
            System.out.println("No WebSocket connection to close.");
        }
    }

    public static void main(String[] args) {
        System.out.println("Launching JavaFX application.");
        launch(args);
    }
}
//ok
