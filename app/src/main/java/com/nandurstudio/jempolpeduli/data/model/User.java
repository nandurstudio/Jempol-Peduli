package com.nandurstudio.jempolpeduli.data.model;

public class User {

    private String address;
    private String phoneNumber;
    private String nickname;
    private String photoUrl;

    public User(String address, String phoneNumber, String nickname, String photoUrl) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

    // Getter and setter methods
    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
