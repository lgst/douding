package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.ddgj.dd.view.CustomGridView;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class OrderDetailActivity extends BaseActivity implements NetWorkInterface, View.OnClickListener, AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private CircleImageView mCivUserIcon;
    private AppCompatTextView mTvUserName;
    private AppCompatTextView mTvOrderName;
    private AppCompatTextView mTvOrderPrice;
    private AppCompatTextView mTvOrderStatus;
    private AppCompatTextView mTvDate;
    private AppCompatTextView mTvAddress;
    private TextView mTvAmount;
    private TextView mTvTime;
    private TextView mTvDetail;
    private CustomGridView mImages;
    private Button mGetOrderBtn;
    private TextView mTvPhone;
    private TextView mTvEmail;
    private LinearLayout mLlConcat;
    private Order mOrder;
    private ArrayList<String> mImagesList;
    private RelativeLayout rlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initData();
    }

    private void initData() {
        OkHttpUtils.get().url(GET_ORDER_DETAIL + "?made_id=" + getIntent().getStringExtra("id")).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        mOrder = new Gson().fromJson(jo.getString("data"), Order.class);
                        mImagesList = new ArrayList<String>();
                        if (mOrder.getMade_picture() != null) {
                            String[] strings = mOrder.getMade_picture().split(",");
                            for (String s : strings) {
                                if (!s.equals("null")) {
                                    mImagesList.add(HOST + "/" + s);
                                }
                            }
                        }
                        initView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initView() {
        User user = UserHelper.getInstance().getUser();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("订制详情");
        mToolbar.setTitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCivUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        Glide.with(this).load(HOST + "/" + mOrder.getHead_picture()).error(R.mipmap.ic_account_circle_grey600_24dp).into(mCivUserIcon);
        mTvUserName = (AppCompatTextView) findViewById(R.id.tv_user_name);
        mTvUserName.setText(mOrder.getAccount());
        mTvOrderName = (AppCompatTextView) findViewById(R.id.tv_order_name);
        mTvOrderName.setText(mOrder.getMade_title());
        mTvOrderPrice = (AppCompatTextView) findViewById(R.id.tv_order_price);
        mTvOrderPrice.setText("￥" + mOrder.getMade_price());
        mTvOrderStatus = (AppCompatTextView) findViewById(R.id.tv_order_status);
        mTvOrderStatus.setText(mOrder.getMade_state());
        mTvDate = (AppCompatTextView) findViewById(R.id.tv_date);
        mTvDate.setText(mOrder.getMade_time());
        mTvAddress = (AppCompatTextView) findViewById(R.id.tv_address);
        mTvAddress.setText(mOrder.getMade_u_address());
        mTvAmount = (TextView) findViewById(R.id.tv_amount);
        mTvAmount.setText(mOrder.getMade_amount());
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvTime.setText(mOrder.getMade_cycle());
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mTvDetail.setText(mOrder.getMade_describe());
        mImages = (CustomGridView) findViewById(R.id.images);
        if (mImagesList != null)
            mImages.setAdapter(new ImageGVAdapter());
        mImages.setOnItemClickListener(this);
        mGetOrderBtn = (Button) findViewById(R.id.get_order_btn);
        mGetOrderBtn.setOnClickListener(this);
        mLlConcat = (LinearLayout) findViewById(R.id.ll_concat);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mTvPhone.setText(mOrder.getMade_u_contact());
        mTvPhone.setOnClickListener(this);
        mTvEmail = (TextView) findViewById(R.id.tv_IM);
        mTvEmail.setOnClickListener(this);
        if (user != null && !user.getAccount().equals(mOrder.getAccount())) {//判断是否为自己的需求，如果不是，显示底部操作按钮
            if (user.getAccount_id().equals(mOrder.getMade_o_u_id())) {//已经接单，显示联系方式
//                mGetOrderBtn.setVisibility(View.GONE);
                mLlConcat.setVisibility(View.VISIBLE);
            } else {//没有接单显示接单按钮
                mGetOrderBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_order_btn:
                getOrder();
                break;
            case R.id.tv_phone:
                call();
                break;
            case R.id.tv_IM:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mOrder.getAccount()));
    }

    private void call() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("tel:" + mOrder.getMade_u_contact()));
        intent.setAction(Intent.ACTION_DIAL);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, PreviewImageActivity.class)
                .putExtra(PreviewImageActivity.PARAMAS_POSITION, position)
                .putStringArrayListExtra(PreviewImageActivity.PARAMAS_IMAGES, mImagesList));
    }

    private void getOrder() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("made_id", mOrder.getMade_id());
        params.put("o_c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        OkHttpUtils.post().url(ADD_ORDER).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        mGetOrderBtn.setVisibility(View.GONE);
                        mLlConcat.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
            Glide.with(OrderDetailActivity.this).load(mImagesList.get(position))
                    .into(iv);
            iv.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    DensityUtil.dip2px(OrderDetailActivity.this, 100)));
            return iv;
        }
    }
}
