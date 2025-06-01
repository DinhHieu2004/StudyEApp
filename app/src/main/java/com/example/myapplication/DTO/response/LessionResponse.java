package com.example.myapplication.DTO.response;

public class LessionResponse {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String imageUrl;

    private Long topicId;

    private String topicName;

    public LessionResponse() {
    }

    public LessionResponse(Long id, String title, String description, String level, String imageUrl, Long topicId, String topicName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.level = level;
        this.imageUrl = imageUrl;
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}

