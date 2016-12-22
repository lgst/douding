package com.ddgj.dd.bean;

/**
 * Created by LG on 2016/12/4.
 */

public class BannerBean {
    private String imageUrl;
    private String title;
    private String text;

    public BannerBean() {
    }

    public BannerBean(String imageUrl, String title, String text) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
