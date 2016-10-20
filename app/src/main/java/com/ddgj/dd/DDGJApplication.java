package com.ddgj.dd;

import android.app.Application;

import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.user.UserHelper;

import com.zhy.http.okhttp.OkHttpUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by Administrator on 2016/9/28.
 */
public class DDGJApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UserHelper.getInstance().initUserInfo(this);
        FileUtil.getInstance().init(getApplicationContext());
        initEM();
        initOkhttp();
    }
    /**
     * 配置网络请求
     */
    private void initOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 环信初始化
     */
    private void initEM() {
   /*     EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);*/
    }
}
