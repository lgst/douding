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
import com.ddgj.dd.bean.Orders;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
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
    private CustomListView mLvUser;
    private boolean isGet;
    private int sum;
    private User user;
    //    0为等待接单 1为已接单 2为成功 3为失败 4服务方申请合作 5服务方申请验收
    private static final String[] STATUS = {"等待接单", "工作中", "交易成功", "交易失败", "待确认合作", "待验收"};
    private int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        user = UserHelper.getInstance().getUser();
        colors = new int[]{R.color.waiting,
                R.color.working,
                R.color.finished,
                R.color.grey,
                R.color.colorPrimary,
                R.color.blue};
        initView();
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
                        if (user != null) {
                            JSONArray ja = jo.getJSONArray("sum");
                            for (int i = 0; i < ja.length(); i++) {
                                Orders o = new Gson().fromJson(ja.getString(i), Orders.class);
                                isGet = o.getO_c_u_id().equals(UserHelper.getInstance().getUser().getAccount_id());
                                sum = i;
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
        mTvUserName.setText(mOrder.getAccount());
        mTvOrderName.setText(mOrder.getMade_title());
        mTvOrderPrice.setText("￥" + mOrder.getMade_price());
        int status = Integer.parseInt(mOrder.getMade_state());
        mTvOrderStatus.setText(STATUS[status]);
        mTvOrderStatus.setBackgroundColor(getResources().getColor(colors[status]));
        mTvDate.setText(mOrder.getMade_time());
        mTvAddress.setText(mOrder.getMade_u_address());
        mTvAmount.setText(mOrder.getMade_amount());
        mTvTime.setText(mOrder.getMade_cycle());
        mTvDetail.setText(mOrder.getMade_describe());
        mTvPhone.setText(mOrder.getMade_u_contact());
        Glide.with(this).load(HOST + "/" + mOrder.getHead_picture()).error(R.mipmap.ic_account_circle_grey600_24dp).into(mCivUserIcon);
        if (mImagesList != null)
            mImages.setAdapter(new ImageGVAdapter());
        if (user != null && !user.getAccount().equals(mOrder.getAccount())) {//判断是否为自己的需求，如果不是，显示底部操作按钮
            if (isGet) {//已经接单，显示联系方式
//                mGetOrderBtn.setVisibility(View.GONE);
                mLlConcat.setVisibility(View.VISIBLE);
            } else {//没有接单显示接单按钮
                mGetOrderBtn.setVisibility(View.VISIBLE);
            }
        }
        if (getIntent().getBooleanExtra("mine", false)) {
            rlBottom.setVisibility(View.GONE);
        } else if (sum == 0) {

        }
    }

    @Override
    protected void initView() {
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
        mTvUserName = (AppCompatTextView) findViewById(R.id.tv_user_name);
        mTvOrderName = (AppCompatTextView) findViewById(R.id.tv_order_name);
        mTvOrderPrice = (AppCompatTextView) findViewById(R.id.tv_order_price);
        mTvOrderStatus = (AppCompatTextView) findViewById(R.id.tv_order_status);
        mTvDate = (AppCompatTextView) findViewById(R.id.tv_date);
        mTvAddress = (AppCompatTextView) findViewById(R.id.tv_address);
        mTvAmount = (TextView) findViewById(R.id.tv_amount);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mImages = (CustomGridView) findViewById(R.id.images);
        mImages.setOnItemClickListener(this);
        mGetOrderBtn = (Button) findViewById(R.id.get_order_btn);
        mGetOrderBtn.setOnClickListener(this);
        mLlConcat = (LinearLayout) findViewById(R.id.ll_concat);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mTvPhone.setOnClickListener(this);
        mTvEmail = (TextView) findViewById(R.id.tv_IM);
        mTvEmail.setOnClickListener(this);
        rlBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        mLvUser = (CustomListView) findViewById(R.id.lv_user);
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
                        showToastLong("接单成功，请联系雇主！");
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
