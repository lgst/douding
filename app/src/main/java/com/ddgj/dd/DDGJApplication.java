package com.ddgj.dd;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.ddgj.dd.db.DBManager;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.user.UserHelper;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by Administrator on 2016/9/28.
 */
public class DDGJApplication extends Application {

    private DBManager dbHelper;

    /**
     * 友盟分享集成
     */
    //各个平台的配置，建议放在全局Application或者程序入口
    {
        Config.REDIRECT_URL = "http://www.qsztx.com";
        //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin("wxb62478d5d6955e1c", "20a2c4b2342614f9cc69c88b0d8b72d7");
        //新浪微博
        PlatformConfig.setSinaWeibo("4227802417", "648d630d8f9126172e64077c6bf383be");
        //QQ、QZone
        PlatformConfig.setQQZone("1105437477", "VFjUid5UIhMt9FoD");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //导入数据库
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        UserHelper.getInstance().initUserInfo(this);//用户初始化
        FileUtil.getInstance().init(getApplicationContext());//目录初始化
        SDKInitializer.initialize(getApplicationContext());//百度地图初始化
        initEM();//环信easeui初始化
        initOkhttp();//OKhttp初始化
    }

    /**
     * 配置网络请求
     */
    private void initOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 环信初始化
     */
    private void initEM() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);
    }
}
