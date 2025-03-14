package com.example;

import org.json.JSONObject;

public class User {
    private int id;
    private String username;
    private String profilpic;
    private String email;
    private Object candidate;
    private Object recruter;
    private String[] posts;
    private String choose;

    // Constructor for direct assignment
    public User(int id, String username, String profilpic, String email, Object candidate, Object recruter, String[] posts, String choose) {
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
        this.email = jsonObject.getString("email");
        this.candidate = jsonObject.isNull("candidate") ? null : jsonObject.get("candidate");
        this.recruter = jsonObject.isNull("recruter") ? null : jsonObject.get("recruter");
        this.posts = jsonObject.isNull("posts") ? new String[0] : jsonObject.getJSONArray("posts").toList().toArray(new String[0]);
        this.choose = jsonObject.isNull("choose") ? null : jsonObject.getString("choose");
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getProfilpic() { return profilpic; }
    public String getEmail() { return email; }
    public Object getCandidate() { return candidate; }
    public Object getRecruter() { return recruter; }
    public String[] getPosts() { return posts; }
    public String getChoose() { return choose; }

    // Fixed getProfilePic() method
    public String getProfilePic() {
        return profilpic;
    }
}
