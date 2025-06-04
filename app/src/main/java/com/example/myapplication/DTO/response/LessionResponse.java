package com.example.myapplication.DTO.response;

public class LessionResponse {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String imageUrl;

    public LessionResponse() {
    }

    public LessionResponse(Long id, String title, String description, String level, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.level = level;
        this.imageUrl = imageUrl;
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
}

