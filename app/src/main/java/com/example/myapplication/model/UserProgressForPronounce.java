package com.example.myapplication.model;

public class UserProgressForPronounce {
    private Long id;
    private User user;
    private Sentence sentence;
    private String userSpeech;
    private int score;
    private String status;
    private boolean isCompleted;
    private long timestamp;
    private int partId;

    // Constructors
    public UserProgressForPronounce() {}

    public UserProgressForPronounce(Sentence sentence, String userSpeech, int score, String status, boolean isCompleted, long timestamp, int partId) {
        this.sentence = sentence;
        this.userSpeech = userSpeech;
        this.score = score;
        this.status = status;
        this.isCompleted = isCompleted;
        this.timestamp = timestamp;
        this.partId = partId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Sentence getSentence() { return sentence; }
    public void setSentence(Sentence sentence) { this.sentence = sentence; }

    public String getUserSpeech() { return userSpeech; }
    public void setUserSpeech(String userSpeech) { this.userSpeech = userSpeech; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }

    @Override
    public String toString() {
        return "UserProgressForPronounce{" +
                "id=" + id +
                ", sentence=" + (sentence != null ? sentence.getId() : null) +
                ", userSpeech='" + userSpeech + '\'' +
                ", score=" + score +
                ", status='" + status + '\'' +
                ", isCompleted=" + isCompleted +
                ", timestamp=" + timestamp +
                ", partId=" + partId +
                '}';
    }
}
