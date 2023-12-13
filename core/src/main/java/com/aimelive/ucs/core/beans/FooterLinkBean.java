package com.aimelive.ucs.core.beans;

public class FooterLinkBean {
    private String label;
    private String url;
    private String target;

    public FooterLinkBean(String label, String url, String target) {
        this.label = label;
        this.url = url;
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getTarget() {
        return target;
    }
}
