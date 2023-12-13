package com.aimelive.ucs.core.beans;

public class NavbarLinkBean {
    private String label;

    private String iconClassName;

    private String elementClass;

    private String linkUrl;

    private Boolean targetBlank;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIconClassName() {
        return iconClassName;
    }

    public void setIconClassName(String iconClassName) {
        this.iconClassName = iconClassName;
    }

    public String getElementClass() {
        return elementClass;
    }

    public void setElementClass(String elementClass) {
        this.elementClass = elementClass;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Boolean getTargetBlank() {
        return targetBlank;
    }

    public void setTargetBlank(Boolean targetBlank) {
        this.targetBlank = targetBlank;
    }

    public String getTarget() {
        return getTargetBlank() == true ? "_blank" : null;
    }
}
