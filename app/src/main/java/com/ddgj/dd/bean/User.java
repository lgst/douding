package com.ddgj.dd.bean;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/10.
 */
public abstract class User implements Serializable {
    protected String account;
    protected String account_id;
    protected String account_type;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public abstract void saveToSharedPreferences(Context context);
    public abstract void initFromSharedPreferences(Context context);

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }
}
