package com.ddgj.dd.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/6.
 */

public class RewardOrder implements Serializable{
    private int id;
    private String reward_task_id;
    private String reward_task_number;
    private String reward_task_content;
    private String reward_task_picture;
    private String r_t_secrecy_state;
    private String r_t_u_name;
    private String r_t_u_phone;
    private String r_t_u_integral;
    private String r_t_u_success_num;
    private String r_t_start_time;
    private String r_t_success_time;
    private String reward_u_id;
    private String reward_id;
    private String r_t_state;
    private String del_state;
    private String reward_title;
    private String reward_picture;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReward_picture() {
        return reward_picture;
    }

    public void setReward_picture(String reward_picture) {
        this.reward_picture = reward_picture;
    }

    public String getReward_title() {
        return reward_title;
    }

    public void setReward_title(String reward_title) {
        this.reward_title = reward_title;
    }

    public String getReward_task_id() {
        return reward_task_id;
    }

    public void setReward_task_id(String reward_task_id) {
        this.reward_task_id = reward_task_id;
    }

    public String getReward_task_number() {
        return reward_task_number;
    }

    public void setReward_task_number(String reward_task_number) {
        this.reward_task_number = reward_task_number;
    }

    public String getReward_task_content() {
        return reward_task_content;
    }

    public void setReward_task_content(String reward_task_content) {
        this.reward_task_content = reward_task_content;
    }

    public String getReward_task_picture() {
        return reward_task_picture;
    }

    public void setReward_task_picture(String reward_task_picture) {
        this.reward_task_picture = reward_task_picture;
    }

    public String getR_t_secrecy_state() {
        return r_t_secrecy_state;
    }

    public void setR_t_secrecy_state(String r_t_secrecy_state) {
        this.r_t_secrecy_state = r_t_secrecy_state;
    }

    public String getR_t_u_name() {
        return r_t_u_name;
    }

    public void setR_t_u_name(String r_t_u_name) {
        this.r_t_u_name = r_t_u_name;
    }

    public String getR_t_u_phone() {
        return r_t_u_phone;
    }

    public void setR_t_u_phone(String r_t_u_phone) {
        this.r_t_u_phone = r_t_u_phone;
    }

    public String getR_t_u_integral() {
        return r_t_u_integral;
    }

    public void setR_t_u_integral(String r_t_u_integral) {
        this.r_t_u_integral = r_t_u_integral;
    }

    public String getR_t_u_success_num() {
        return r_t_u_success_num;
    }

    public void setR_t_u_success_num(String r_t_u_success_num) {
        this.r_t_u_success_num = r_t_u_success_num;
    }

    public String getR_t_start_time() {
        return r_t_start_time;
    }

    public void setR_t_start_time(String r_t_start_time) {
        this.r_t_start_time = r_t_start_time;
    }

    public String getR_t_success_time() {
        return r_t_success_time;
    }

    public void setR_t_success_time(String r_t_success_time) {
        this.r_t_success_time = r_t_success_time;
    }

    public String getReward_u_id() {
        return reward_u_id;
    }

    public void setReward_u_id(String reward_u_id) {
        this.reward_u_id = reward_u_id;
    }

    public String getReward_id() {
        return reward_id;
    }

    public void setReward_id(String reward_id) {
        this.reward_id = reward_id;
    }

    public String getR_t_state() {
        return r_t_state;
    }

    public void setR_t_state(String r_t_state) {
        this.r_t_state = r_t_state;
    }

    public String getDel_state() {
        return del_state;
    }

    public void setDel_state(String del_state) {
        this.del_state = del_state;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(!(obj instanceof RewardOrder)) return false;
        RewardOrder that = (RewardOrder) obj;
        return this.reward_task_id.equals(that.reward_task_id);
    }
}
