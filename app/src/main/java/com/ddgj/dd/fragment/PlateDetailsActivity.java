package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;

/**
 * Created by Administrator on 2016/10/22.
 */
public class PlateDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backUp;

    @Override
    public void initView() {
        backUp = (ImageView) findViewById(R.id.backup);
        backUp.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_deatils);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backup:
                finish();
                break;
            default:
                break;
        }
    }
}
