package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Orders;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class OrdersDetilActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private Toolbar mToolbar;
    private TextView mOrdersId;
    private TextView mOrdersTime;
    private TextView mOrdersStatu;
    private ImageView mImg;
    private TextView mTitle;
    private CardView mCustom;
    private Button mAcceptanceCheck;
    private Orders mOrders;
    private int[] colors;
    private String[] STATUS = {"", "待确认合作", "交易成功", "交易失败", "订单被取消", "验收中", "验收失败", "工作中", "被拒绝合作"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_detil);
        mOrders = (Orders) getIntent().getSerializableExtra("orders");
        initView();
        init();
    }

    private void init() {
        colors = new int[]{R.color.grey,
                R.color.colorPrimary,
                R.color.finished,
                R.color.grey,
                R.color.grey,
                R.color.blue,
                R.color.grey,
                R.color.working,
                R.color.grey};
        mToolbar.setTitle("订单详情");
        mToolbar.setTitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mOrdersId.setText("订单编号：" + mOrders.getOrder_num());
        mOrdersTime.setText("创建时间：" + mOrders.getOrder_create_time());
        int status = Integer.parseInt(mOrders.getOrder_state());
        mOrdersStatu.setText(STATUS[status]);
        mOrdersStatu.setBackgroundResource(colors[status]);
        if (mOrders.getMade_picture() != null) {
            String[] pics = mOrders.getMade_picture().split(",");
            for (int i = 0; i < pics.length; i++) {
                if (pics[i].equals("null"))
                    continue;
                mImg.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(NetWorkInterface.HOST + "/" + pics[i])
                        .into(mImg);
            }
        }
        mTitle.setText(mOrders.getMade_title());
        if (mOrders.getOrder_state().equals("7"))
            mAcceptanceCheck.setVisibility(View.VISIBLE);
    }

    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mOrdersId = (TextView) findViewById(R.id.orders_id);
        mOrdersTime = (TextView) findViewById(R.id.orders_time);
        mOrdersStatu = (TextView) findViewById(R.id.orders_statu);
        mImg = (ImageView) findViewById(R.id.img);
        mTitle = (TextView) findViewById(R.id.title);
        mCustom = (CardView) findViewById(R.id.custom);
        mAcceptanceCheck = (Button) findViewById(R.id.agree_check);

        mCustom.setOnClickListener(this);
        mAcceptanceCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom:
                startActivity(new Intent(this, OrderDetailActivity.class).putExtra("id", mOrders.getMade_id()));
                break;
            case R.id.agree_check:
                acceptanceCheck();
                break;
        }
    }

    private void acceptanceCheck() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("order_id", mOrders.getOrder_id());
        params.put("made_id", mOrders.getMade_id());
        OkHttpUtils.post().url(ACCEPTANCE_CHECK).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", "申请验收出错：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", "申请验收: " + response);
                ResponseInfo resinfo = new Gson().fromJson(response, ResponseInfo.class);
                if (resinfo.getStatus() == 0) {
                    mAcceptanceCheck.setVisibility(View.GONE);
                    showToastLong(resinfo.getMsg());
                }
            }
        });
    }
}
