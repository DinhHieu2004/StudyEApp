package com.example.myapplication.model;

public class Courses {
    private String title;
    private int price;
    private String picPath;

    public Courses(String title, int price, String picPath) {
        this.title = title;
        this.price = price;
        this.picPath = picPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
