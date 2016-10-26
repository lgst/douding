package com.ddgj.dd.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ddgj.dd.R;
import com.ddgj.dd.util.UpdateUtils;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public void initView() {
        Log.i("weiweifabu", "");
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
}
