package com.example.myapplication.DTO;

import java.util.List;

public class SubscriptionPlan {
    private int id;
    private String name;
    private double price;
    private int durationDays;
    private List<String> features;

    public SubscriptionPlan(int id, String name, double price, int durationDays, List<String> features) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
        this.features = features;
    }

    public SubscriptionPlan() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}

