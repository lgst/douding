package com.ddgj.dd.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/2.
 */

public class RewardInfo implements Serializable{

    private int id;
    private String reward_id;
    private String reward_title;
    private String reward_price;
    private String reward_number;
    private String reward_type;
    private String reward_create_time;
    private String reward_start_time;
    private String reward_end_time;
    private String reward_cycle;
    private String reward_pattern;
    private String reward_require;
    private String reward_picture;
    private String reward_u_phone;
    private String reward_u_name;
    private String reward_u_id;
    private String reward_task_id;
    private String reward_state;
    private String reward_task_num;
    private String reward_attention_num;
    private String del_state;
    private String account;
    private int min_price;
    private int max_price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getReward_id() {
        return reward_id;
    }

    public void setReward_id(String reward_id) {
        this.reward_id = reward_id;
    }

    public String getReward_title() {
        return reward_title;
    }

    public void setReward_title(String reward_title) {
        this.reward_title = reward_title;
    }

    public String getReward_price() {
        return reward_price;
    }

    public void setReward_price(String reward_price) {
        this.reward_price = reward_price;
    }

    public String getReward_number() {
        return reward_number;
    }

    public void setReward_number(String reward_number) {
        this.reward_number = reward_number;
    }

    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public String getReward_create_time() {
        return reward_create_time;
    }

    public void setReward_create_time(String reward_create_time) {
        this.reward_create_time = reward_create_time;
    }

    public String getReward_start_time() {
        return reward_start_time;
    }

    public void setReward_start_time(String reward_start_time) {
        this.reward_start_time = reward_start_time;
    }

    public String getReward_end_time() {
        return reward_end_time;
    }

    public void setReward_end_time(String reward_end_time) {
        this.reward_end_time = reward_end_time;
    }

    public String getReward_cycle() {
        return reward_cycle;
    }

    public void setReward_cycle(String reward_cycle) {
        this.reward_cycle = reward_cycle;
    }

    public String getReward_pattern() {
        return reward_pattern;
    }

    public void setReward_pattern(String reward_pattern) {
        this.reward_pattern = reward_pattern;
    }

    public String getReward_require() {
        return reward_require;
    }

    public void setReward_require(String reward_require) {
        this.reward_require = reward_require;
    }

    public String getReward_picture() {
        return reward_picture;
    }

    public void setReward_picture(String reward_picture) {
        this.reward_picture = reward_picture;
    }

    public String getReward_u_phone() {
        return reward_u_phone;
    }

    public void setReward_u_phone(String reward_u_phone) {
        this.reward_u_phone = reward_u_phone;
    }

    public String getReward_u_name() {
        return reward_u_name;
    }

    public void setReward_u_name(String reward_u_name) {
        this.reward_u_name = reward_u_name;
    }

    public String getReward_u_id() {
        return reward_u_id;
    }

    public void setReward_u_id(String reward_u_id) {
        this.reward_u_id = reward_u_id;
    }

    public String getReward_task_id() {
        return reward_task_id;
    }

    public void setReward_task_id(String reward_task_id) {
        this.reward_task_id = reward_task_id;
    }

    public String getReward_state() {
        return reward_state;
    }

    public void setReward_state(String reward_state) {
        this.reward_state = reward_state;
    }

    public String getReward_task_num() {
        return reward_task_num;
    }

    public void setReward_task_num(String reward_task_num) {
        this.reward_task_num = reward_task_num;
    }

    public String getReward_attention_num() {
        return reward_attention_num;
    }

    public void setReward_attention_num(String reward_attention_num) {
        this.reward_attention_num = reward_attention_num;
    }

    public String getDel_state() {
        return del_state;
    }

    public void setDel_state(String del_state) {
        this.del_state = del_state;
    }

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }

    public int getMax_price() {
        return max_price;
    }

    public void setMax_price(int max_price) {
        this.max_price = max_price;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(!(obj instanceof RewardInfo)) return false;
        RewardInfo that = (RewardInfo) obj;
        return that.getReward_id().equals(that.getReward_id());
    }
}
