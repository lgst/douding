package com.ddgj.dd.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.StringUtils;

public class SettingsActivity extends BaseActivity {
    private TextView mCachaSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    @Override
    public void initView() {
        mCachaSize = (TextView) findViewById(R.id.cacha_size);
        mCachaSize.setText(StringUtils.getSize(FileUtil.getInstance().getCacheSize()));
    }

    public void backClick(View v) {
        finish();
    }

    public void clearClick(View v) {
        FileUtil.getInstance().clearCache();
        mCachaSize.setText("0");
    }
}
