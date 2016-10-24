package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.activity.WebActivity;
import com.ddgj.dd.adapter.PatentPLVAdapter;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/20.
 */

public class MinePatentFragment extends BaseFragment implements NetWorkInterface{
    private PullToRefreshListView mplv;
    private LinearLayout mLoading;
    private List<Patent> mPatents = new ArrayList<Patent>();
    private PatentPLVAdapter mAdapter;
    private BaseActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        return inflater.inflate(R.layout.fragment_mine_patent, null);
    }

    @Override
    protected void initView() {
        mplv = (PullToRefreshListView)findViewById(R.id.list);
        mLoading = (LinearLayout)findViewById(R.id.loading);
        mplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final Patent originality = mPatents.get(position-1);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("client_side", "app");
                        params.put("patent_id", originality.getPatent_id());
                        OkHttpUtils.post().url(GET_PATENT_DETAILS).params(params).build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e("lgst", "获取创意详情页失败：" + e.getMessage());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                                if (responseInfo.getStatus() == STATUS_SUCCESS) {
                                    String url = responseInfo.getData();
                                    Log.e("lgst", url);
                                    startActivity(new Intent(activity, WebActivity.class)
                                            .putExtra("title", originality.getPatent_name())
                                            .putExtra("url", HOST + url));
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    /**
     * @param: flag：数据加载方式  LOAD：重新加载  UPDATE：加载更多
     * classes:分类  ALL：全部   NEW：最新   HOT：最热   MINE：我的
     */
    private void initData() {
        if (!activity.checkNetWork()) {
            activity.showToastNotNetWork();
            return;
        }
        if(mPatents.size()>0)
        {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "1000");
        params.put("p_account_id", UserHelper.getInstance().getUser().getAccount_id());
//        params.put("originality_differentiate",String.valueOf(0));

        OkHttpUtils.post().url(GET_MINE_PATENT).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                activity.showToastNotNetWork();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", response);
                try {
                    JSONObject jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (status == STATUS_SUCCESS) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String patentStr = ja.getJSONObject(i).toString();
                            Patent originality = new Gson().fromJson(patentStr, Patent.class);
                            mPatents.add(originality);
                        }
                            mAdapter = new PatentPLVAdapter(activity, mPatents);
                            mplv.setAdapter(mAdapter);
                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
                            mLoading.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
