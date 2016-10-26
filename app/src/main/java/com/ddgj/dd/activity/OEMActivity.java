package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ddgj.dd.R;
import com.ddgj.dd.util.net.NetWorkInterface;

public class OEMActivity extends BaseActivity implements View.OnClickListener,NetWorkInterface {

    private ImageView back;
    private ImageView factory;
    private ImageView product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oem);
        initView();
    }

    @Override
    public void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        factory = (ImageView) findViewById(R.id.factory);
        factory.setOnClickListener(this);
        product = (ImageView) findViewById(R.id.product);
        product.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.factory:
                startActivity(new Intent(this,OEMFactoryActivity.class).putExtra("classes","1"));
                break;
            case R.id.product:
                startActivity(new Intent(this, OEMProductActivity.class));
                break;
        }
    }
}
