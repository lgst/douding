package com.ddgj.dd.bean;

/**
 * Created by Administrator on 2016/10/26.
 */

public class Comment {

    /**
     * c_id : d88d7c76-544c-43e1-aaf5-4c7988d77aa7
     * topic_id :
     * topic_type : 0
     * c_content : 测试
     * from_u_id :
     * comment_time : 2016-10-26 16:03:27
     * pageNumber : 0
     * pageSingle : 0
     * nickname : 豆丁
     * head_picture : img/picture10.jpg
     * account : admin007
     */

    private String c_id;
    private String topic_id;
    private String topic_type;
    private String c_content;
    private String from_u_id;
    private String comment_time;
    private int pageNumber;
    private int pageSingle;
    private String nickname;
    private String head_picture;
    private String account;

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_type() {
        return topic_type;
    }

    public void setTopic_type(String topic_type) {
        this.topic_type = topic_type;
    }

    public String getC_content() {
        return c_content;
    }

    public void setC_content(String c_content) {
        this.c_content = c_content;
    }

    public String getFrom_u_id() {
        return from_u_id;
    }

    public void setFrom_u_id(String from_u_id) {
        this.from_u_id = from_u_id;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSingle() {
        return pageSingle;
    }

    public void setPageSingle(int pageSingle) {
        this.pageSingle = pageSingle;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_picture() {
        return head_picture;
    }

    public void setHead_picture(String head_picture) {
        this.head_picture = head_picture;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
