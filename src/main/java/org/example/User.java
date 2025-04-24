package com.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String profilpic;
    private String email;
    private Object candidate;
    private Object recruter;
    private List<com.example.Post> posts; // Changed from String[] to List<Post>
    private String choose;

    // Constructor for direct assignment
    public User(int id, String username, String profilpic, String email, Object candidate, Object recruter, List<com.example.Post> posts, String choose) {
        this.id = id;
        this.username = username;
        this.profilpic = profilpic;
        this.email = email;
        this.candidate = candidate;
        this.recruter = recruter;
        this.posts = posts;
        this.choose = choose;
    }

    // Constructor for parsing JSON response
    public User(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        this.username = jsonObject.getString("username");
        this.profilpic = jsonObject.optString("profilpic", null);
        this.email = jsonObject.optString("email", "N/A");
        this.candidate = jsonObject.isNull("candidate") ? null : jsonObject.get("candidate");
        this.recruter = jsonObject.isNull("recruter") ? null : jsonObject.get("recruter");
        this.choose = jsonObject.optString("choose", null);

        // ✅ Fixing the posts issue
        this.posts = new ArrayList<>();
        if (jsonObject.has("posts") && !jsonObject.isNull("posts")) {
            JSONArray postsArray = jsonObject.getJSONArray("posts");
            for (int i = 0; i < postsArray.length(); i++) {
                this.posts.add(new com.example.Post(postsArray.getJSONObject(i))); // Convert to Post object
            }
        }
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getProfilpic() { return profilpic; }
    public String getEmail() { return email; }
    public Object getCandidate() { return candidate; }
    public Object getRecruter() { return recruter; }
    public List<com.example.Post> getPosts() { return posts; } // Changed return type
    public String getChoose() { return choose; }

    public String getProfilePic() {
        return profilpic;
    }
}
//okay
