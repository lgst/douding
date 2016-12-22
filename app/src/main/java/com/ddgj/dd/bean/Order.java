package com.ddgj.dd.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/17.
 */

public class Order implements Serializable {

    /**
     * made_id : 2
     * made_name : 订制名称
     * made_title : 订制标题
     * made_type_id :
     * made_price : 订制价格
     * made_amount :
     * made_cycle :
     * made_time : 订制时间
     * made_specifications :
     * made_describe :
     * made_note :
     * made_u_name :
     * made_u_contact :
     * made_u_email :
     * made_u_address : 订制地址
     * made_picture :
     * made_state :
     * m_browse_amount :
     * m_comment_amount :
     * m_a_id :
     * made_differentiate :
     * head_picture : files/20161012/6223620090673535.png
     * pageNumber :
     * pageSingle :
     */

    private int id;
    private String made_id;
    private String made_name;
    private String made_title;
    private String made_type_id;
    private String made_price;
    private String made_amount;
    private String made_cycle;
    private String made_time;
    private String made_specifications;
    private String made_describe;
    private String made_note;
    private String made_u_name;
    private String made_u_contact;
    private String made_u_email;
    private String made_u_address;
    private String made_picture;
    private String made_state;
    private String m_browse_amount;
    private String m_comment_amount;
    private String m_a_id;
    private String made_differentiate;
    private String head_picture;
    private String pageNumber;
    private String pageSingle;
    private String account;
    private String city;
    private String made_o_u_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMade_o_u_id() {
        return made_o_u_id;
    }

    public void setMade_o_u_id(String made_o_u_id) {
        this.made_o_u_id = made_o_u_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMade_id() {
        return made_id;
    }

    public void setMade_id(String made_id) {
        this.made_id = made_id;
    }

    public String getMade_name() {
        return made_name;
    }

    public void setMade_name(String made_name) {
        this.made_name = made_name;
    }

    public String getMade_title() {
        return made_title;
    }

    public void setMade_title(String made_title) {
        this.made_title = made_title;
    }

    public String getMade_type_id() {
        return made_type_id;
    }

    public void setMade_type_id(String made_type_id) {
        this.made_type_id = made_type_id;
    }

    public String getMade_price() {
        return made_price;
    }

    public void setMade_price(String made_price) {
        this.made_price = made_price;
    }

    public String getMade_amount() {
        return made_amount;
    }

    public void setMade_amount(String made_amount) {
        this.made_amount = made_amount;
    }

    public String getMade_cycle() {
        return made_cycle;
    }

    public void setMade_cycle(String made_cycle) {
        this.made_cycle = made_cycle;
    }

    public String getMade_time() {
        return made_time;
    }

    public void setMade_time(String made_time) {
        this.made_time = made_time;
    }

    public String getMade_specifications() {
        return made_specifications;
    }

    public void setMade_specifications(String made_specifications) {
        this.made_specifications = made_specifications;
    }

    public String getMade_describe() {
        return made_describe;
    }

    public void setMade_describe(String made_describe) {
        this.made_describe = made_describe;
    }

    public String getMade_note() {
        return made_note;
    }

    public void setMade_note(String made_note) {
        this.made_note = made_note;
    }

    public String getMade_u_name() {
        return made_u_name;
    }

    public void setMade_u_name(String made_u_name) {
        this.made_u_name = made_u_name;
    }

    public String getMade_u_contact() {
        return made_u_contact;
    }

    public void setMade_u_contact(String made_u_contact) {
        this.made_u_contact = made_u_contact;
    }

    public String getMade_u_email() {
        return made_u_email;
    }

    public void setMade_u_email(String made_u_email) {
        this.made_u_email = made_u_email;
    }

    public String getMade_u_address() {
        return made_u_address;
    }

    public void setMade_u_address(String made_u_address) {
        this.made_u_address = made_u_address;
    }

    public String getMade_picture() {
        return made_picture;
    }

    public void setMade_picture(String made_picture) {
        this.made_picture = made_picture;
    }

    public String getMade_state() {
        return made_state;
    }

    public void setMade_state(String made_state) {
        this.made_state = made_state;
    }

    public String getM_browse_amount() {
        return m_browse_amount;
    }

    public void setM_browse_amount(String m_browse_amount) {
        this.m_browse_amount = m_browse_amount;
    }

    public String getM_comment_amount() {
        return m_comment_amount;
    }

    public void setM_comment_amount(String m_comment_amount) {
        this.m_comment_amount = m_comment_amount;
    }

    public String getM_a_id() {
        return m_a_id;
    }

    public void setM_a_id(String m_a_id) {
        this.m_a_id = m_a_id;
    }

    public String getMade_differentiate() {
        return made_differentiate;
    }

    public void setMade_differentiate(String made_differentiate) {
        this.made_differentiate = made_differentiate;
    }

    public String getHead_picture() {
        return head_picture;
    }

    public void setHead_picture(String head_picture) {
        this.head_picture = head_picture;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageSingle() {
        return pageSingle;
    }

    public void setPageSingle(String pageSingle) {
        this.pageSingle = pageSingle;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(!(obj instanceof Order)) return false;
        Order that = (Order) obj;
        return this.made_id.equals(that.getMade_id());
    }
}
