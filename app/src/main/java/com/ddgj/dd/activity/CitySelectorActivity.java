package com.ddgj.dd.activity;

import android.os.Bundle;
import android.util.Log;

import com.ddgj.dd.R;
import com.ddgj.dd.view.city_selector.CityPicker;

public class CitySelectorActivity extends BaseActivity {

    private com.ddgj.dd.view.city_selector.CityPicker citypicker;
    private android.widget.RelativeLayout activitycityselector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selector);
        initViews();
    }

    @Override
    public void initViews() {
        this.citypicker = (CityPicker) findViewById(R.id.citypicker);
        citypicker.setOnSelectingListener(new CityPicker.OnSelectingListener() {
            @Override
            public void selected(boolean selected) {
                Log.i("lgst",citypicker.getCity_string());
            }
        });
    }
}
