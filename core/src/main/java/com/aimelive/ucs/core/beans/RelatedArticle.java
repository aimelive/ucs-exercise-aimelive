package com.aimelive.ucs.core.beans;

public class RelatedArticle {
    private String id;
    private String text;
    private String date;
    private String hashtag;
    private String image;
    private String link;

    // Constructor
    public RelatedArticle(String id, String text, String date, String hashtag, String image, String link) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.hashtag = hashtag;
        this.image = image;
        this.link = link;
    }

    // Getter for 'id'
    public String getId() {
        return id;
    }

    // Getter for 'text'
    public String getText() {
        return text;
    }

    // Getter for 'date'
    public String getDate() {
        return date;
    }

    // Getter for 'hashtag'
    public String getHashtag() {
        return hashtag;
    }

    // Getter for 'image'
    public String getImage() {
        return image;
    }

    // Getter for 'link'
    public String getLink() {
        return link;
    }

}
