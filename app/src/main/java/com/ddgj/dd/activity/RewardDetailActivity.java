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
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.BusEvent;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CustomGridView;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RewardDetailActivity extends BaseActivity implements NetWorkInterface, View.OnClickListener, AdapterView.OnItemClickListener {

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
    private RewardInfo mReward;
    private LinearLayout mBottom;
    private ArrayList<String> mImages = new ArrayList<>();
    private CustomGridView mImgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);
        initView();
        initData();
    }


    protected void initView() {
        mReward = (RewardInfo) getIntent().getSerializableExtra("data");
        String[] imgs = mReward.getReward_picture().split(",");
        for (String url : imgs) {
            if (url.equals("null"))
                continue;
            mImages.add(HOST + "/" + url);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("悬赏详情");
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
                ImageView iv = new ImageView(RewardDetailActivity.this);
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

    private void initData() {
        if (UserHelper.getInstance().getUser() != null &&
                UserHelper.getInstance().getUser().getAccount_id().equals(mReward.getReward_u_id())) {
            mBottom.setVisibility(View.GONE);
        }
        mTnederTitle.setText(mReward.getReward_title());
        mTenderNumber.setText(mReward.getReward_number());
        mStartTime.setText(StringUtils.getEndTime(mReward.getReward_create_time(), mReward.getReward_cycle()));
        mTimer.setText(StringUtils.getTime(mReward.getReward_create_time(), mReward.getReward_cycle()));
        mTenderDetail.setText(mReward.getReward_require());
        mTenderMod.setText(mReward.getReward_type());
        DecimalFormat df = new DecimalFormat("#.##");
        String price = df.format((Float.parseFloat(mReward.getReward_price()) * 0.985f));
        mMoney.setText(mReward.getReward_price() + " ｘ 98.5%\n=\n" + price);
        if (mReward.getDel_state()==null||mReward.getDel_state().equals("0"))
            mStatus.setText("招标中");
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
                startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mReward.getAccount()));
                break;
            case R.id.join:
                join();
                break;
        }
    }

    private void join() {

        Map<String, String> params = new HashMap<>();
        params.put("reward_id", mReward.getReward_id());
        params.put("reward_u_id", UserHelper.getInstance().getUser().getAccount_id());
        OkHttpUtils.post().url(ADD_REWARD_ORDER).params(params).build().execute(new StringCallback() {
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
                        Snackbar.make(mToolbar, "参加成功\n请在规定时间内提交任务作品\n在“我的”可查看任务！", Snackbar.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new BusEvent(BusEvent.REWARD_ORDER));
                    } else if (jo.getInt("status") == 1) {
                        Snackbar.make(mToolbar, "您已参加过该悬赏任务！\n请转到“我的”进行后续操作！", Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {
            startActivity(new Intent(this, RewardDetailActivity.class).putExtra("data", mReward));
            finish();
            logi("刷新");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, PreviewImageActivity.class)
                .putExtra(PreviewImageActivity.PARAMAS_POSITION, position)
                .putStringArrayListExtra(PreviewImageActivity.PARAMAS_IMAGES, mImages));
    }
}
