package com.example.myapplication.DTO.response;

public class UserResponse {
    private String uid;
    private String email;
    private String name;
    private String phone;
    private String dob;

    public UserResponse(String uid, String email, String name, String dob, String phone) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
    }

    public UserResponse() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
