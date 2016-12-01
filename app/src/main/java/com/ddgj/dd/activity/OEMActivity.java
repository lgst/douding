package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ddgj.dd.R;
import com.ddgj.dd.util.net.NetWorkInterface;

public class OEMActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private ImageView factory;
    private ImageView product;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oem);
        initView();
    }

    @Override
    public void initView() {
        factory = (ImageView) findViewById(R.id.factory);
        factory.setOnClickListener(this);
        product = (ImageView) findViewById(R.id.product);
        product.setOnClickListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("委托代工");
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setTitleTextColor(Color.parseColor("#014886"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.factory:
                startActivity(new Intent(this, OEMFactoryActivity.class));
                break;
            case R.id.product:
                startActivity(new Intent(this, OEMProductActivity.class));
                break;
        }
    }
}
