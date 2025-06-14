package com.example.myapplication.DTO.response;

import java.io.Serializable;

public class LessionResponse implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String imageUrl;

    private String audioUrl;

    private Long topicId;

    private String topicName;

    public LessionResponse() {
    }

    public LessionResponse(Long id, String title, String description, String level, String imageUrl, String audioUrl, Long topicId, String topicName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.level = level;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
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

