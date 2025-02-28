package com.example;

public class User {
    private int id;
    private String username;
    private String email;
    private String profilpic;
    private Object candidate;
    private Object recruter;
    private String[] posts;
    private Object choose;

    // Constructor
    public User(int id, String username, String email, String profilpic, Object candidate, Object recruter, String[] posts, Object choose) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilpic = profilpic;
        this.candidate = candidate;
        this.recruter = recruter;
        this.posts = posts;
        this.choose = choose;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilpic() {
        return profilpic;
    }

    public void setProfilpic(String profilpic) {
        this.profilpic = profilpic;
    }

    public Object getCandidate() {
        return candidate;
    }

    public void setCandidate(Object candidate) {
        this.candidate = candidate;
    }

    public Object getRecruter() {
        return recruter;
    }

    public void setRecruter(Object recruter) {
        this.recruter = recruter;
    }

    public String[] getPosts() {
        return posts;
    }

    public void setPosts(String[] posts) {
        this.posts = posts;
    }

    public Object getChoose() {
        return choose;
    }

    public void setChoose(Object choose) {
        this.choose = choose;
    }
}
