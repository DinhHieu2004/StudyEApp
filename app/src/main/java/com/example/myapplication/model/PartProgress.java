package com.example.myapplication.model;

public class PartProgress {
    private int partId;
    private String partName;
    private int totalSentences;
    private int completedSentences;
    private double completionPercentage;
    private long lastUpdated;

    public PartProgress(int partId, String partName, int totalSentences, int completedSentences, double completionPercentage, long lastUpdated) {
        this.partId = partId;
        this.partName = partName;
        this.totalSentences = totalSentences;
        this.completedSentences = completedSentences;
        this.completionPercentage = completionPercentage;
        this.lastUpdated = lastUpdated;
    }

    public PartProgress(){}

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getTotalSentences() {
        return totalSentences;
    }

    public void setTotalSentences(int totalSentences) {
        this.totalSentences = totalSentences;
    }

    public int getCompletedSentences() {
        return completedSentences;
    }

    public void setCompletedSentences(int completedSentences) {
        this.completedSentences = completedSentences;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}