package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

public class PostService {
    private static final String API_URL = "https://chakrihub-1-cilx.onrender.com/Post";

    public static CompletableFuture<List<com.example.Posts>> fetchPosts() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    List<com.example.Posts> posts = new ArrayList<>();
                    if (response.statusCode() == 200) {
                        JSONArray jsonArray = new JSONArray(response.body());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            posts.add(new com.example.Posts(jsonArray.getJSONObject(i)));
                        }
                    } else {
                        System.out.println("⚠️ Failed to fetch posts. Status: " + response.statusCode());
                    }
                    return posts;
                });
    }
}
