package com.ddgj.dd.bean;

/**
 * Created by Administrator on 2016/11/23.
 * 订单实体类
 */

public class Orders {

    /**
     * order_id : 63f0c5c4-66b2-43b6-8757-43c85e660de0
     * order_num : 2
     * order_create_time : 2016-11-23 10:24:55
     * order_success_time :
     * order_state : 1
     * o_c_u_id : 10b9182e-8a95-44a7-b12f-27984ce95105
     * made_id : 14bc0b10-9cea-4c29-b3b4-69493c800bc0
     * made_title : 明后
     * made_picture : files/20161102/32753134303476.jpg,null,null,null,null,null,null,null,null
     * pageNumber : 0
     * pageSingle : 0
     */

    private String order_id;
    private String order_num;
    private String order_create_time;
    private String order_success_time;
    private String order_state;
    private String o_c_u_id;
    private String made_id;
    private String made_title;
    private String made_picture;
    private int pageNumber;
    private int pageSingle;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getOrder_create_time() {
        return order_create_time;
    }

    public void setOrder_create_time(String order_create_time) {
        this.order_create_time = order_create_time;
    }

    public String getOrder_success_time() {
        return order_success_time;
    }

    public void setOrder_success_time(String order_success_time) {
        this.order_success_time = order_success_time;
    }

    public String getOrder_state() {
        return order_state;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public String getO_c_u_id() {
        return o_c_u_id;
    }

    public void setO_c_u_id(String o_c_u_id) {
        this.o_c_u_id = o_c_u_id;
    }

    public String getMade_id() {
        return made_id;
    }

    public void setMade_id(String made_id) {
        this.made_id = made_id;
    }

    public String getMade_title() {
        return made_title;
    }

    public void setMade_title(String made_title) {
        this.made_title = made_title;
    }

    public String getMade_picture() {
        return made_picture;
    }

    public void setMade_picture(String made_picture) {
        this.made_picture = made_picture;
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
}
