package com.aimelive.ucs.core.beans;

import java.util.List;

public class ArticleData {
    private String title;
    private String description;
    private String image;
    private String link;
    private List<String> tags;

    // public ArticleData(String title, String description, String image, String
    // link, List<String> tags) {
    // this.title = title;
    // this.description = description;
    // this.image = image;
    // this.link = link;
    // this.tags = tags;
    // }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
