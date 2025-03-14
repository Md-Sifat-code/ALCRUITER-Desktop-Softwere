package com.example;

import org.json.JSONObject;

public class Post {
    private int id;
    private String body;
    private String picture;
    private String createdDate;
    private String updatedDate;

    public Post(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        this.body = jsonObject.optString("body", "");
        this.picture = jsonObject.optString("picture", null);
        this.createdDate = jsonObject.optString("createdDate", "");
        this.updatedDate = jsonObject.optString("updatedDate", null);
    }

    public int getId() { return id; }
    public String getBody() { return body; }
    public String getPicture() { return picture; }
    public String getCreatedDate() { return createdDate; }
    public String getUpdatedDate() { return updatedDate; }
}
