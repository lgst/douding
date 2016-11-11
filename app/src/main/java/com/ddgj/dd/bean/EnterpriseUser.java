package com.ddgj.dd.bean;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * 企业用户
 * Created by lyg on 2016/10/5.
 */
public class EnterpriseUser extends User implements Serializable {

    public String getFacilitator_head() {
        return facilitator_head;
    }

    public void setFacilitator_head(String facilitator_head) {
        this.facilitator_head = facilitator_head;
    }

    /**
     * password : 123123
     * account : 用户名
     * acilitator_id :
     * account_id : 用户id-5cd0-4ac3-806c-fa8b9342dee2
     * account_type : 用户类型 0：个人  1：企业
     * facilitator_name : 企业名称
     * facilitator_scale :
     * facilitator_field :
     * facilitator_area :
     * facilitator_address :
     * facilitator_contact :
     * facilitator_linkman :
     * facilitator_email :
     * facilitator_license :
     * facilitator_picture :
     * facilitator_ip :
     * facilitator_landing_time :
     * add_time : 2016-10-05 10:12:34
     * modify_time :
     *
     * private String acilitator_id;//服务商ID
     private String facilitator_name;//服务商名称
     private String facilitator_scale;//服务商规模
     private String facilitator_field;//服务商领域
     private String facilitator_area;//服务商区域
     private String facilitator_address;//服务商详细地址
     private String facilitator_contact;//服务商联系方式
     private String facilitator_linkman;//服务商联系人
     private String facilitator_email;//服务商邮箱
     private String facilitator_license;//服务商营业执照
     private String facilitator_picture;//服务商展示图片
     private String facilitator_ip;//服务商登陆IP
     private String facilitator_landing_time;//服务商登陆时间
     private String add_time;//信息添加时间
     private String modify_time;//信息更新时间
     private String account_id;//账号ID
     *
     *
     */

    private String facilitator_head;
    private String password;
    private String acilitator_id;
    private String facilitator_name;
    private String facilitator_scale;
    private String facilitator_field;
    private String facilitator_area;
    private String facilitator_address;
    private String facilitator_contact;
    private String facilitator_linkman;
    private String facilitator_email;
    private String facilitator_license;
    private String facilitator_picture;
    private String facilitator_ip;
    private String facilitator_landing_time;
    private String add_time;
    private String modify_time;
    private String modify_differentiate;

    /**
     * 保存到SharedPreferences
     */
    public void saveToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("password", password)
                .putString("account", account)
                .putString("acilitator_id", acilitator_id)
                .putString("account_id", account_id)
                .putString("account_type", account_type)
                .putString("facilitator_name", facilitator_name)
                .putString("facilitator_scale", facilitator_scale)
                .putString("facilitator_field", facilitator_field)
                .putString("facilitator_area", facilitator_area)
                .putString("facilitator_address", facilitator_address)
                .putString("facilitator_contact", facilitator_contact)
                .putString("facilitator_linkman", facilitator_linkman)
                .putString("facilitator_email", facilitator_email)
                .putString("facilitator_license", facilitator_license)
                .putString("facilitator_picture", facilitator_picture)
                .putString("facilitator_ip", facilitator_ip)
                .putString("facilitator_landing_time", facilitator_landing_time)
                .putString("add_time", add_time)
                .putString("head_picture", head_picture)
                .putString("modify_differentiate", modify_differentiate)
                .putString("modify_time", modify_time).commit();
    }

    /**
     * 从SharedPreferences获取企业用户信息
     */
    public void initFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");
        account = sharedPreferences.getString("account", "");
        acilitator_id = sharedPreferences.getString("acilitator_id", "");
        account_id = sharedPreferences.getString("account_id", "");
        account_type = sharedPreferences.getString("account_type", "");
        facilitator_name = sharedPreferences.getString("facilitator_name", "");
        facilitator_scale = sharedPreferences.getString("facilitator_scale", "");
        facilitator_field = sharedPreferences.getString("facilitator_field", "");
        facilitator_area = sharedPreferences.getString("facilitator_area", "");
        facilitator_address = sharedPreferences.getString("facilitator_address", "");
        facilitator_contact = sharedPreferences.getString("facilitator_contact", "");
        facilitator_linkman = sharedPreferences.getString("facilitator_linkman", "");
        facilitator_email = sharedPreferences.getString("facilitator_email", "");
        facilitator_license = sharedPreferences.getString("facilitator_license", "");
        facilitator_picture = sharedPreferences.getString("facilitator_picture", "");
        facilitator_ip = sharedPreferences.getString("facilitator_ip", "");
        modify_differentiate = sharedPreferences.getString("modify_differentiate", "");
        facilitator_landing_time = sharedPreferences.getString("facilitator_landing_time", "");
        add_time = sharedPreferences.getString("add_time", "");
        modify_time = sharedPreferences.getString("modify_time", "");
        head_picture = sharedPreferences.getString("head_picture", "");
    }

    public String getModify_differentiate() {
        return modify_differentiate;
    }

    public void setModify_differentiate(String modify_differentiate) {
        this.modify_differentiate = modify_differentiate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAcilitator_id() {
        return acilitator_id;
    }

    public void setAcilitator_id(String acilitator_id) {
        this.acilitator_id = acilitator_id;
    }

    public String getFacilitator_name() {
        return facilitator_name;
    }

    public void setFacilitator_name(String facilitator_name) {
        this.facilitator_name = facilitator_name;
    }

    public String getFacilitator_scale() {
        return facilitator_scale;
    }

    public void setFacilitator_scale(String facilitator_scale) {
        this.facilitator_scale = facilitator_scale;
    }

    public String getFacilitator_field() {
        return facilitator_field;
    }

    public void setFacilitator_field(String facilitator_field) {
        this.facilitator_field = facilitator_field;
    }

    public String getFacilitator_area() {
        return facilitator_area;
    }

    public void setFacilitator_area(String facilitator_area) {
        this.facilitator_area = facilitator_area;
    }

    public String getFacilitator_address() {
        return facilitator_address;
    }

    public void setFacilitator_address(String facilitator_address) {
        this.facilitator_address = facilitator_address;
    }

    public String getFacilitator_contact() {
        return facilitator_contact;
    }

    public void setFacilitator_contact(String facilitator_contact) {
        this.facilitator_contact = facilitator_contact;
    }

    public String getFacilitator_linkman() {
        return facilitator_linkman;
    }

    public void setFacilitator_linkman(String facilitator_linkman) {
        this.facilitator_linkman = facilitator_linkman;
    }

    public String getFacilitator_email() {
        return facilitator_email;
    }

    public void setFacilitator_email(String facilitator_email) {
        this.facilitator_email = facilitator_email;
    }

    public String getFacilitator_license() {
        return facilitator_license;
    }

    public void setFacilitator_license(String facilitator_license) {
        this.facilitator_license = facilitator_license;
    }

    public String getFacilitator_picture() {
        return facilitator_picture;
    }

    public void setFacilitator_picture(String facilitator_picture) {
        this.facilitator_picture = facilitator_picture;
    }

    public String getFacilitator_ip() {
        return facilitator_ip;
    }

    public void setFacilitator_ip(String facilitator_ip) {
        this.facilitator_ip = facilitator_ip;
    }

    public String getFacilitator_landing_time() {
        return facilitator_landing_time;
    }

    public void setFacilitator_landing_time(String facilitator_landing_time) {
        this.facilitator_landing_time = facilitator_landing_time;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }
}
