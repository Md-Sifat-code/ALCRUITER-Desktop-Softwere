package com.example;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class StompClient extends WebSocketClient {

    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private Consumer<String> onMessageHandler;

    public StompClient(URI serverUri) {
        super(serverUri);
    }

    /** Setter for a message callback to notify UI or other components */
    public void setOnMessageHandler(Consumer<String> handler) {
        this.onMessageHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket connection opened");
        // Send STOMP CONNECT frame
        String connectFrame = "CONNECT\n" +
                "accept-version:1.2\n" +
                "heart-beat:10000,10000\n\n" +
                "\u0000";
        send(connectFrame);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        if (message.startsWith("CONNECTED")) {
            System.out.println("STOMP connected");
            // Subscribe to the user's queue after successful connection
            String subscriptionId = "sub-" + idCounter.incrementAndGet();
            String subscribeFrame = "SUBSCRIBE\n" +
                    "id:" + subscriptionId + "\n" +
                    "destination:/user/queue/messages\n\n" +
                    "\u0000";
            send(subscribeFrame);

        } else if (message.startsWith("MESSAGE")) {
            // Notify UI or caller with the message body
            if (onMessageHandler != null) {
                onMessageHandler.accept(message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed with exit code " + code + ", info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred: " + ex.getMessage());
        ex.printStackTrace();
    }
}
