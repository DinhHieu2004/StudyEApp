package com.example.myapplication.DTO.response;

public class PartProgressDTO {
    private int partNumber;
    private int totalSentences;
    private int completedSentences;
    private double completionPercentage;
    private long lastUpdated;

    // Constructors
    public PartProgressDTO() {}

    public PartProgressDTO(int partNumber, int totalSentences, int completedSentences, double completionPercentage, long lastUpdated) {
        this.partNumber = partNumber;
        this.totalSentences = totalSentences;
        this.completedSentences = completedSentences;
        this.completionPercentage = completionPercentage;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getPartNumber() { return partNumber; }
    public void setPartNumber(int partNumber) { this.partNumber = partNumber; }

    public int getTotalSentences() { return totalSentences; }
    public void setTotalSentences(int totalSentences) { this.totalSentences = totalSentences; }

    public int getCompletedSentences() { return completedSentences; }
    public void setCompletedSentences(int completedSentences) { this.completedSentences = completedSentences; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    @Override
    public String toString() {
        return "PartProgressDTO{" +
                "partNumber=" + partNumber +
                ", totalSentences=" + totalSentences +
                ", completedSentences=" + completedSentences +
                ", completionPercentage=" + completionPercentage +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}