package com.example;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class WebSocketClientManager {
    private static Socket socket;

    public static void connect(String userId) {
        try {
            IO.Options options = new IO.Options();
            options.query = "userId=" + userId; // Attach userId as query parameter

            // Use http, no /ws path
            socket = IO.socket("http://chakrihub-0qv1.onrender.com", options);

            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("✅ Connected to WebSocket Server");
            });

            socket.on("message", args -> {
                System.out.println("💬 Message received: " + args[0]);
            });

            socket.connect();

        } catch (URISyntaxException e) {
            System.out.println("❌ WebSocket connection error: " + e.getMessage());
        }
    }

    public static void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            socket.close();
            System.out.println("🔴 Disconnected from WebSocket");
        }
    }

    public static Socket getSocket() {
        return socket;
    }
}
