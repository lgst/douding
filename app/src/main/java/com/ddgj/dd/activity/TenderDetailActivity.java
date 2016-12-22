package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.TenderInfo;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CustomGridView;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.ADD_TENDER_ORDER;
import static com.ddgj.dd.util.net.NetWorkInterface.HOST;

public class TenderDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private Toolbar mToolbar;
    private TextView mTnederTitle;
    private TextView mTenderNumber;
    private TextView mStartTime;
    private TextView mTimer;
    private TextView mTenderDetail;
    private TextView mTenderMod;
    private TextView mMoney;
    private TextView mStatus;
    private TextView mChat;
    private TextView mJoin;
    private TenderInfo mTender;
    private LinearLayout mBottom;
    private ArrayList<String> mImages = new ArrayList<>();
    private CustomGridView mImgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_detail);
        initView();
        initData();
    }


    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("招标详情");
        mToolbar.setTitleTextColor(Color.parseColor("#014886"));
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTnederTitle = (TextView) findViewById(R.id.tneder_title);
        mTenderNumber = (TextView) findViewById(R.id.tender_number);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mTimer = (TextView) findViewById(R.id.timer);
        mTenderDetail = (TextView) findViewById(R.id.tender_detail);
        mTenderMod = (TextView) findViewById(R.id.tender);
        mMoney = (TextView) findViewById(R.id.money);
        mStatus = (TextView) findViewById(R.id.status);
        mChat = (TextView) findViewById(R.id.chat);
        mJoin = (TextView) findViewById(R.id.join);
        mChat.setOnClickListener(this);
        mJoin.setOnClickListener(this);
        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mImgs = (CustomGridView) findViewById(R.id.imgs);
        mImgs.setOnItemClickListener(this);
    }

    private void initData() {
        mTender = (TenderInfo) getIntent().getSerializableExtra("data");
        String[] imgs = mTender.getInvite_t_picture().split(",");
        for (String url : imgs) {
            if (url.equals("null"))
                continue;
            mImages.add(HOST + "/" + url);
        }
        mTnederTitle.setText(mTender.getInvite_t_title());
        mTenderNumber.setText(mTender.getInvite_t_number());
        mStartTime.setText(mTender.getInvite_t_c_time());
        mTimer.setText(StringUtils.getTime(mTender.getInvite_t_c_time(), mTender.getInvite_t_cycle()));
        mTenderDetail.setText(mTender.getInvite_t_require());
        mTenderMod.setText(mTender.getInvite_t_type());
        DecimalFormat df = new DecimalFormat("#.##");
        String price = df.format((Float.parseFloat(mTender.getInvite_t_price()) * 0.985f));
        mMoney.setText(mTender.getInvite_t_price() + " ｘ 98.5%\n=\n" + price);
        mStatus.setText(mTender.getInvite_t_state());
        if (UserHelper.getInstance().isLogined())
            if (mTender.getInvite_t_u_id().equals(UserHelper.getInstance().getUser().getAccount_id())) {
                mBottom.setVisibility(View.GONE);
            }
        mImgs.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mImages.size();
            }

            @Override
            public Object getItem(int position) {
                return mImages.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView iv = new ImageView(TenderDetailActivity.this);
                iv.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        DensityUtil.dip2px(getApplicationContext(), 64)));
                Glide.with(getApplicationContext())
                        .load(mImages.get(position))
                        .into(iv);
                return iv;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {//登录成功后刷新界面
            startActivity(new Intent(this, TenderDetailActivity.class).putExtra("data", mTender));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (!UserHelper.getInstance().isLogined()) {
            startActivityForResult(new Intent(this, LoginActivity.class).putExtra("flag", LoginActivity.BACK), 1);
            showToastShort("请先登录！");
            return;
        }
        switch (v.getId()) {
            case R.id.chat:
                startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mTender.getAccount()));
                break;
            case R.id.join:
                Map<String, String> params = new HashMap<String, String>();
                params.put("invite_t_id", mTender.getInvite_t_id());
                params.put("invite_u_id", UserHelper.getInstance().getUser().getAccount_id());
                OkHttpUtils.post()
                        .params(params)
                        .url(ADD_TENDER_ORDER)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                loge("投标失败：" + e.getMessage());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                logi("投标成功：" + response);
                                try {
                                    int statu = new JSONObject(response).getInt("status");
                                    if (statu == 0) {
                                        Snackbar.make(mToolbar, "投标成功！", Snackbar.LENGTH_SHORT)
                                                .setAction("查看", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        startActivity(new Intent(TenderDetailActivity.this, JoinTenderActivity.class)
                                                                .putExtra("data", mTender));
                                                    }
                                                }).show();
                                    } else {
                                        Snackbar.make(mToolbar, "您已经投过标！\n请转到”我的“进行后续操作！", Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, PreviewImageActivity.class)
                .putExtra(PreviewImageActivity.PARAMAS_POSITION, position)
                .putStringArrayListExtra(PreviewImageActivity.PARAMAS_IMAGES, mImages));
    }
}
