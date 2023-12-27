package com.aimelive.ucs.core.beans;

public class ArticleTagBean {
    private String tag;
    private String extractedTag;

    public ArticleTagBean(String tag, String extractedTag) {
        this.tag = tag;
        this.extractedTag = extractedTag;
    }

    public String getTag() {
        return tag;
    }

    public String getExtractedTag() {
        return extractedTag;
    }

}
