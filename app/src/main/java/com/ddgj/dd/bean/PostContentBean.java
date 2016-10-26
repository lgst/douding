package com.ddgj.dd.bean;

/**
 * Created by Administrator on 2016/10/26.
 */

public class PostContentBean {
    public PostContentBean(int order, String content) {
        this.order = order;
        this.content = content;
    }

    public int order;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String content;
}
