package com.aimelive.ucs.core.beans;

public class SocialLinkBean {
    private String icon;
    private String url;

    public SocialLinkBean(String icon, String url) {
        this.icon = icon;
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }
}
