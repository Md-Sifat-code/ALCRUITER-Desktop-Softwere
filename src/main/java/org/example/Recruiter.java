package com.example;

import org.json.JSONObject;

public class Recruiter {
    private int id;
    private String name;
    private String phoneNumber;
    private String coverPhoto;
    private String companyDiscription;
    private String companyName;
    private String industryType;
    private String officeLocation;
    private String bio;

    // Constructor for direct assignment
    public Recruiter(int id, String name, String phoneNumber, String coverPhoto, String companyDiscription, String companyName, String industryType, String officeLocation, String bio) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.coverPhoto = coverPhoto;
        this.companyDiscription = companyDiscription;
        this.companyName = companyName;
        this.industryType = industryType;
        this.officeLocation = officeLocation;
        this.bio = bio;
    }

    // Constructor for parsing JSON response
    public Recruiter(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.optString("name", "N/A");
        this.phoneNumber = jsonObject.optString("phoneNumber", "N/A");
        this.coverPhoto = jsonObject.optString("coverPhoto", "N/A");
        this.companyDiscription = jsonObject.optString("companyDiscription", "N/A");
        this.companyName = jsonObject.optString("companyName", "N/A");
        this.industryType = jsonObject.optString("industryType", "N/A");
        this.officeLocation = jsonObject.optString("officeLocation", "N/A");
        this.bio = jsonObject.optString("bio", "N/A");
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCoverPhoto() { return coverPhoto; }
    public String getCompanyDiscription() { return companyDiscription; }
    public String getCompanyName() { return companyName; }
    public String getIndustryType() { return industryType; }
    public String getOfficeLocation() { return officeLocation; }
    public String getBio() { return bio; }
}
