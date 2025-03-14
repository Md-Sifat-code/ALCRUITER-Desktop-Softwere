package com.example;

import org.json.JSONObject;

public class Posts {
    private int id;
    private String body;
    private String photo;
    private String createdDate;
    private String updatedDate;
    private com.example.User user;

    public Posts(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        this.body = jsonObject.optString("body", "");
        this.photo = jsonObject.optString("photo", null);
        this.createdDate = jsonObject.optString("createdDate", "");
        this.updatedDate = jsonObject.optString("updatedDate", null);

        if (jsonObject.has("user") && !jsonObject.isNull("user")) {
            this.user = new com.example.User(jsonObject.getJSONObject("user"));
        }
    }

    public int getId() { return id; }
    public String getBody() { return body; }
    public String getPhoto() { return photo; }
    public String getCreatedDate() { return createdDate; }
    public String getUpdatedDate() { return updatedDate; }
    public com.example.User getUser() { return user; }
}
