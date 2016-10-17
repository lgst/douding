package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class OrderListActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mTvCity;
    private TextView mTvPrice;
    private PullToRefreshListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        initView();
        LinearLayout activityorderlist = (LinearLayout) findViewById(R.id.activity_order_list);
        PullToRefreshListView list = (PullToRefreshListView) findViewById(R.id.list);
        TextView tvprice = (TextView) findViewById(R.id.tv_price);
        TextView tvcity = (TextView) findViewById(R.id.tv_city);
    }

    @Override
    public void initViews() {

    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.back);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mList = (PullToRefreshListView) findViewById(R.id.list);

        mBack.setOnClickListener(this);
        mTvCity.setOnClickListener(this);
        mTvPrice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_city:
                startActivityForResult(new Intent(this,CitySelectorActivity.class),1);
                break;
            case R.id.tv_price:

                break;
        }
    }
}
