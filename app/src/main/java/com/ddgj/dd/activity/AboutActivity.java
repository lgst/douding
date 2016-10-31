package com.ddgj.dd.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.util.UpdateUtils;


public class AboutActivity extends BaseActivity {
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public void initView() {
        version = (TextView) findViewById(R.id.version);
        version.setText("当前版本："+getVersionName());
    }

    public void backClick(View v) {
        finish();
    }

    public void checkUpdate(View v) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        new UpdateUtils(this).checkVersion();
    }

    /**
     * 获取版本号
     */
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }
}
