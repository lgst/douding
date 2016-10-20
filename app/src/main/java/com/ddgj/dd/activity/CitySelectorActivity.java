package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ddgj.dd.R;
import com.ddgj.dd.view.city_selector.CityPicker;

public class CitySelectorActivity extends BaseActivity {

    private CityPicker citypicker;
    private RelativeLayout activitycityselector;
    private LinearLayout mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selector);
        initView();
    }

    @Override
    public void initView() {
        this.citypicker = (CityPicker) findViewById(R.id.citypicker);
        citypicker.setOnSelectingListener(new CityPicker.OnSelectingListener() {
            @Override
            public void selected(boolean selected) {
                Log.i("lgst", citypicker.getCity_string());
            }
        });
        mContent = (LinearLayout) findViewById(R.id.content);
    }

    public void backClick(View v) {
        setResult(FAILDE);
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    public void okClick(View v) {
        setResult(SUCCESS, new Intent().putExtra("city", citypicker.getCity_string()));
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            overridePendingTransition(0, R.anim.slide_out_to_bottom);
        return super.onKeyDown(keyCode, event);

    }
}
