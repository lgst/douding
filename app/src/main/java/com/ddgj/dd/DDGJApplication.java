package com.ddgj.dd;

import android.app.Application;

import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.user.UserHelper;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

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
