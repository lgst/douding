package com.ddgj.dd.util;

import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.ResponseInfo;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON 工具类
 * Created by lyg on 2016/10/5.
 */
public class JsonUtils {
    public static ResponseInfo getResponse(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        String data = jsonObject .getString("data");
        String count = jsonObject.getString("count");
        int status = jsonObject.getInt("status");
        String sum = jsonObject.getString("sum");
        String msg = jsonObject.getString("msg");
        return new ResponseInfo(data,count,status,sum,msg);
    }
    public static Object getUser(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        String accountType = jsonObject.getString("account_type");
        if(accountType.equals("0")){
            //个人用户
            PersonalUser personalUser = new Gson().fromJson(jsonStr,PersonalUser.class);
            return personalUser;
        }else{
            //企业用户
            EnterpriseUser enterpriseUser = new Gson().fromJson(jsonStr,EnterpriseUser.class);
            return enterpriseUser;
        }
    }
}
