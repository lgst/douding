package com.ddgj.dd.util.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.FileUtil;

/**
 * 用户操作类，使用前必须初始化调用init()
 * Created by Administrator on 2016/10/5.
 */
public class UserHelper {
    private static UserHelper instance;
    /**上下文环境*/
    private Context context;
    /**用户实体*/
    private User user;
    /**登录状态*/
    private boolean logined;
    /**第一次初始化标志位*/
    private static boolean isFirstInit = true;
    private UserHelper(){
    }

    /**初始化，使用前必须初始化*/
    public void initUserInfo(Context context) {
        if(!isFirstInit)
        {
            return;
        }
        this.context = context;
        loadUserInfo();
        isFirstInit = false;
    }

    public void loadUserInfo()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        String accountType = sharedPreferences.getString("account_type","");
        if(accountType.equals("1")){
            //企业用户
            user = new EnterpriseUser();
            user.initFromSharedPreferences(context);
            setLogined(true);
        }else if(accountType.equals("0")){
            //个人用户
            user = new PersonalUser();
            user.initFromSharedPreferences(context);
            setLogined(true);
        }else{
            //未登录
            setLogined(false);
        }
    }

    public User getUser() {
        return user;
    }

    /**登出操作*/
    public void logout()
    {
        //删除登录数据
        context.getSharedPreferences("user",Context.MODE_PRIVATE).edit().clear().commit();
        //清除登录标志
        setLogined(false);
        //清除用户对象
        user = null;
        FileUtil.getInstance().deleteUserIcon();
        Toast.makeText(context,context.getResources().getString(R.string.logout_success),Toast.LENGTH_SHORT).show();
    }

    /**获取实例*/
    public static UserHelper getInstance(){
        if(instance == null)
        {
            instance = new UserHelper();
        }
        return instance;
    }

    /**获取登录状态*/
    public boolean isLogined() {
        return logined;
    }

    /**设置登录状态*/
    public void setLogined(boolean logined) {
        this.logined = logined;
    }
}
