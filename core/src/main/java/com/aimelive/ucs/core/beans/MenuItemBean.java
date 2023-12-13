package com.aimelive.ucs.core.beans;

import java.util.List;

public class MenuItemBean {
    private String title;
    private String path;
    private String name;

    private List<MenuItemBean> subItems;

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuItemBean> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<MenuItemBean> subItems) {
        this.subItems = subItems;
    }
}
