package com.ddgj.dd;

import android.app.Activity;
import android.app.Application;

import com.ddgj.dd.util.user.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */
public class DDGJApplication extends Application {
    private static List<Activity> activityList = new ArrayList<Activity>();
    @Override
    public void onCreate() {
        super.onCreate();
        UserHelper.getInstance().initUserInfo(this);
    }

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activityList.remove(activity);
    }

    public static void removeAllActivity(){
        for (Activity act: activityList) {
            act.finish();
        }
        activityList.clear();
    }
}
