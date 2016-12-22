package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OrderFactoryAdapter;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
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

public class OrderFactoryActivity extends BaseActivity implements NetWorkInterface {

    private PullToRefreshListView mplv;
    private LinearLayout mLoading;
    private List<EnterpriseUser> mFactorys = new ArrayList<EnterpriseUser>();
    /**
     * 页码
     */
    private int mPageNumber = 1;
    /**
     * 数量
     */
    private int mPageSingle = 15;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;
    /**
     * 更新数据
     */
    private static final int UPDATE = 2;
    private OrderFactoryAdapter mAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_factory);
        initView();
        initData(LOAD);
    }

    private void initData(final int flag) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
        params.put("modify_differentiate", "0");

        OkHttpUtils.post().url(GET_ORDER_FACTORY).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mPageNumber--;
                mplv.onRefreshComplete();
                showToastNotNetWork();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", response);
                try {
                    JSONObject jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (status == STATUS_SUCCESS) {
                        JSONArray ja = jo.getJSONArray("data");
                        if (flag == LOAD) {
                            mFactorys.clear();
                        }
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getJSONObject(i).toString();
                            Log.i("lgst", str);
                            EnterpriseUser factory = new Gson().fromJson(str, EnterpriseUser.class);
                            mFactorys.add(factory);
                        }
                        if (flag == LOAD) {
                            mAdapter = new OrderFactoryAdapter(mFactorys);
                            mplv.setAdapter(mAdapter);
                        } else {
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                        }
                        if (mplv.isRefreshing())//关闭刷新
                            mplv.onRefreshComplete();
                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
                            mLoading.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("订制工厂");
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setTitleTextColor(Color.parseColor("#014886"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mplv = (PullToRefreshListView) findViewById(R.id.list);
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mplv.setMode(PullToRefreshBase.Mode.BOTH);
        mplv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                initData(UPDATE);
            }
        });
        mplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final EnterpriseUser user = mFactorys.get(position - 1);
                Intent intent = new Intent(OrderFactoryActivity.this, FactoryDetailActivity.class);
                intent.putExtra("acilitator_id",user.getAcilitator_id());
                startActivity(intent);
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("client_side", "app");
//                params.put("acilitator_id", user.getAccount_id());
//                OkHttpUtils.post().url(GET_ORDER_FACTORY_DETAILS).params(params).build().execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Log.e("lgst", "获取代工工厂详情页失败：" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        Log.i("lgst", response);
//                        ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
//                        if (responseInfo.getStatus() == STATUS_SUCCESS) {
//                            String url = responseInfo.getData();
//                            Log.e("lgst", url);
//                            startActivity(new Intent(OrderFactoryActivity.this, WebActivity.class)
//                                    .putExtra("title", user.getFacilitator_name())
//                                    .putExtra("url", HOST + url));
//                        }
//                    }
//                });
            }
        });
    }
}
