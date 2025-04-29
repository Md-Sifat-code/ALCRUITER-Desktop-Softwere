package com.example;

import org.json.JSONObject;

public class Candidate {
    private int id;
    private String fullName;
    private String location;
    private String language;
    private String educationalQualifications;
    private String skills;
    private String phoneNumber;
    private String yearsOfExperience;
    private String preferedPossion;
    private String portfolioLinks;
    private String pastExperience;
    private String bio;
    private String about;
    private String cv;
    private String coverPic;

    // Constructor for direct assignment
    public Candidate(int id, String fullName, String location, String language, String educationalQualifications,
                     String skills, String phoneNumber, String yearsOfExperience, String preferedPossion,
                     String portfolioLinks, String pastExperience, String bio, String about, String cv, String coverPic) {
        this.id = id;
        this.fullName = fullName;
        this.location = location;
        this.language = language;
        this.educationalQualifications = educationalQualifications;
        this.skills = skills;
        this.phoneNumber = phoneNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.preferedPossion = preferedPossion;
        this.portfolioLinks = portfolioLinks;
        this.pastExperience = pastExperience;
        this.bio = bio;
        this.about = about;
        this.cv = cv;
        this.coverPic = coverPic;
    }

    // Constructor for parsing from JSONObject
    public Candidate(JSONObject json) {
        this.id = json.optInt("id", 0);
        this.fullName = json.optString("fullName", "N/A");
        this.location = json.optString("location", "N/A");
        this.language = json.optString("language", "N/A");
        this.educationalQualifications = json.optString("educationalQualifications", "N/A");
        this.skills = json.optString("skills", "N/A");
        this.phoneNumber = json.optString("phoneNumber", "N/A");
        this.yearsOfExperience = json.optString("yearsOfExperience", "N/A");
        this.preferedPossion = json.optString("preferedPossion", "N/A");
        this.portfolioLinks = json.optString("portfolioLinks", "N/A");
        this.pastExperience = json.optString("pastExperience", "N/A");
        this.bio = json.optString("bio", "N/A");
        this.about = json.optString("about", "N/A");
        this.cv = json.optString("cv", "N/A");
        this.coverPic = json.optString("coverPic", "https://via.placeholder.com/400x150");
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getLocation() { return location; }
    public String getLanguage() { return language; }
    public String getEducationalQualifications() { return educationalQualifications; }
    public String getSkills() { return skills; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getYearsOfExperience() { return yearsOfExperience; }
    public String getPreferedPossion() { return preferedPossion; }
    public String getPortfolioLinks() { return portfolioLinks; }
    public String getPastExperience() { return pastExperience; }
    public String getBio() { return bio; }
    public String getAbout() { return about; }
    public String getCv() { return cv; }
    public String getCoverPic() { return coverPic; }
}
