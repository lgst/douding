package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.RewardAdapter;
import com.ddgj.dd.adapter.TenderAdapter;
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.bean.TenderInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CustomListView;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class RewardActivity extends BaseActivity implements NetWorkInterface, View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mReward;
    private TextView mTender;
    private TextView mAllRewards;
    private CustomListView mRewardList;
    private TextView mAllTenders;
    private CustomListView mTendersList;
    private List<RewardInfo> mRewards;
    private List<TenderInfo> mTenders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        mRewards = new ArrayList<RewardInfo>();
        mTenders = new ArrayList<>();
        initView();
        initData();
    }

    private void initData() {
        StringBuilder sb = new StringBuilder(GET_REWARD)
                .append("?pageNumber=1&pageSingle=2&reward_state=&reward_price=&reward_title=&reward_type=");
        OkHttpUtils.get().url(sb.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("获取推荐悬赏失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
//                logi("获取推荐悬赏成功：" + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            RewardInfo reward = new Gson().fromJson(str, RewardInfo.class);
                            mRewards.add(reward);
                        }
                        setData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        OkHttpUtils.get().url(GET_TENDER + "?pageNumber=1&pageSingle=2").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("获取招标失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            TenderInfo tender = new Gson().fromJson(str, TenderInfo.class);
                            mTenders.add(tender);
                        }
                        setTenders();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTenders() {
        mTendersList.setAdapter(new TenderAdapter(mTenders));
    }

    private void setData() {
        mRewardList.setAdapter(new RewardAdapter(mRewards));
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("悬赏招标");
        mToolbar.setTitleTextColor(Color.parseColor("#014886"));
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mReward = (TextView) findViewById(R.id.reward);
        mReward.setOnClickListener(this);
        mTender = (TextView) findViewById(R.id.tender);
        mTender.setOnClickListener(this);
        mAllRewards = (TextView) findViewById(R.id.all_rewards);
        mAllRewards.setOnClickListener(this);
        mRewardList = (CustomListView) findViewById(R.id.reward_list);
        mAllTenders = (TextView) findViewById(R.id.all_tenders);
        mAllTenders.setOnClickListener(this);
        mTendersList = (CustomListView) findViewById(R.id.tenders_list);
        mTendersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TenderInfo tenderInfo = mTenders.get(position);
                startActivity(new Intent(RewardActivity.this, TenderDetailActivity.class).putExtra("data", tenderInfo));
            }
        });
        mRewardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RewardInfo rewardInfo = mRewards.get(position);
                startActivity(new Intent(RewardActivity.this, RewardDetailActivity.class).putExtra("data", rewardInfo));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tender:
                startActivity(new Intent(this, TenderListActivity.class));
                break;
            case R.id.reward:
                startActivity(new Intent(this, RewardListActivity.class));
                break;
        }
    }
}
