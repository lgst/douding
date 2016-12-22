package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.bean.RewardOrder;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class JoinRewardActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mNumber;
    private TextView mEndTime;
    private TextView mTimer;
    private Button mCommit;
    private LinearLayout mActivityJoinReward;
    private RewardInfo mReward;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_reward);
        initData();
    }

    private void initData() {
        RewardOrder ro = (RewardOrder) getIntent().getSerializableExtra("data");
        OkHttpUtils.get().url(NetWorkInterface.HOST + "/findRewardById.do?reward_id=" + ro.getReward_id()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("获取悬赏失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                logi("获取悬赏成功：" + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        String str = jo.getString("data");
                        mReward = new Gson().fromJson(str, RewardInfo.class);
                        initView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNumber = (TextView) findViewById(R.id.number);
        mNumber.setText(mReward.getReward_number());
        mEndTime = (TextView) findViewById(R.id.end_time);
        mEndTime.setText(StringUtils.getEndTime(mReward.getReward_create_time(), mReward.getReward_cycle()));
        mTimer = (TextView) findViewById(R.id.timer);
        mTimer.setText(StringUtils.getTime(mReward.getReward_create_time(), mReward.getReward_cycle()));
        mCommit = (Button) findViewById(R.id.commit);
        mActivityJoinReward = (LinearLayout) findViewById(R.id.activity_join_reward);
        mCommit.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(mReward.getReward_title());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                commit();
                break;
        }
    }

    private void commit() {
        startActivityForResult(new Intent(this, CommitRewardActivity.class).putExtra("data", mReward),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==SUCCESS)
            finish();
    }
}
