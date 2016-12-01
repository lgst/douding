package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CircleImageView;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class OEMDetailActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private Toolbar mToolbar;
    private CircleImageView mCivUserIcon;
    private AppCompatTextView mTvUserName;
    private AppCompatTextView mTvOrderName;
    private AppCompatTextView mTvOrderPrice;
    private AppCompatTextView mTvDate;
    private AppCompatTextView mTvAddress;
    private TextView mTvAmount;
    private TextView mTvTime;
    private TextView mTvDetail;
    private CustomGridView mImages;
    private CustomListView mLvUser;
    private Button mGetOrderBtn;
    private TextView mTvPhone;
    private TextView mTvIM;
    private LinearLayout mLlConcat;
    private RelativeLayout mRlBottom;
    private LinearLayout mActivityOrderDetail;
    private Order mOEM;
    private ArrayList<String> mImagesList = new ArrayList<>();
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oemdetail);
        initView();
        initData();
    }

    private void initData() {
        OkHttpUtils.get()
                .url(GET_ORDER_BY_ID + "?made_id=" + getIntent().getStringExtra("id"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loge(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        logi(response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if (jo.getInt("status") == 0) {
                                mOEM = new Gson().fromJson(jo.getString("data"), Order.class);
                                if (mOEM.getMade_picture() != null) {
                                    String[] imgs = mOEM.getMade_picture().split(",");
                                    for (String url : imgs) {
                                        if(!url.equals("null")){
                                            mImagesList.add(url);
                                        }
                                    }
                                }
                                setData();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setData() {
        mTvUserName.setText(mOEM.getMade_u_name());
        mTvOrderName.setText(mOEM.getMade_title());
        mTvOrderPrice.setText(mOEM.getMade_price());
        mTvTime.setText(mOEM.getMade_cycle());
        mTvAddress.setText(mOEM.getMade_u_address());
        mTvAmount.setText(mOEM.getMade_amount());
        mTvDate.setText(mOEM.getMade_time());
        mTvDetail.setText(mOEM.getMade_describe());
        mTvPhone.setText(mOEM.getMade_u_contact());
        for (String img : mOEM.getHead_picture().split(",")) {
            if (!img.equals("null"))
                Glide.with(getApplicationContext()).load(HOST + "/" + img).into(mCivUserIcon);
        }
        mAdapter = new ImageGVAdapter();
        mImages.setAdapter(mAdapter);
    }

    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setTitle("代工需求详情");
        mToolbar.setTitleTextColor(Color.WHITE);
        mCivUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        mTvUserName = (AppCompatTextView) findViewById(R.id.tv_user_name);
        mTvOrderName = (AppCompatTextView) findViewById(R.id.tv_order_name);
        mTvOrderPrice = (AppCompatTextView) findViewById(R.id.tv_order_price);
        mTvDate = (AppCompatTextView) findViewById(R.id.tv_date);
        mTvAddress = (AppCompatTextView) findViewById(R.id.tv_address);
        mTvAmount = (TextView) findViewById(R.id.tv_amount);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mImages = (CustomGridView) findViewById(R.id.images);
        mLvUser = (CustomListView) findViewById(R.id.lv_user);
        mGetOrderBtn = (Button) findViewById(R.id.get_order_btn);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mTvIM = (TextView) findViewById(R.id.tv_IM);
        mLlConcat = (LinearLayout) findViewById(R.id.ll_concat);
        mRlBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        mActivityOrderDetail = (LinearLayout) findViewById(R.id.activity_order_detail);
        mGetOrderBtn.setOnClickListener(this);
        mTvPhone.setOnClickListener(this);
        mTvIM.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone:
                call();
                break;
            case R.id.tv_IM:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        startActivity(new Intent(this, ChatActivity.class)
                .putExtra(EaseConstant.EXTRA_USER_ID, mOEM.getAccount()));
    }

    private void call() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("tel:" + mOEM.getMade_u_contact()));
        intent.setAction(Intent.ACTION_DIAL);
        startActivity(intent);
    }

    class ImageGVAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mImagesList.size();
        }

        @Override
        public Object getItem(int position) {
            return mImagesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(OEMDetailActivity.this).load(mImagesList.get(position))
                    .into(iv);
            iv.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    DensityUtil.dip2px(OEMDetailActivity.this, 100)));
            return iv;
        }
    }
}
