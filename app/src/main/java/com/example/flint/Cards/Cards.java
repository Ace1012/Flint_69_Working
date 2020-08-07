package com.example.flint.Cards;

public class Cards {

    private String userID;
    private String name;
    private String profileImageUrl;

    public Cards(String userID, String name, String profileImageUrl) {
        this.userID = userID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
