package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OrderAdapter;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OEMProductActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private PullToRefreshListView mplv;
    private LinearLayout mLoading;
    private List<Order> mOrders = new ArrayList<Order>();
    /**
     * 页码
     */
    private int mPageNumber = 1;
    /**
     * 数量
     */
    private int mPageSingle = 10;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;
    /**
     * 更新数据
     */
    private static final int UPDATE = 2;
    private OrderAdapter mAdapter;
    private FloatingActionButton mFab;
    private HttpHelper<Order> mOrderHttpHelper;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oemproduct);
        mOrders = new ArrayList<Order>();
        mOrderHttpHelper = new HttpHelper<Order>(this, Order.class);
        mAdapter = new OrderAdapter(mOrders);
        initView();
        initData(LOAD);
        mplv.setAdapter(mAdapter);
        mplv.setRefreshing();
    }

    private void initData(final int flag) {
        //获取代工工厂
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("made_state", "0");
        params.put("made_differentiate", "1");
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
        mOrderHttpHelper.getDatasPost(GET_ORDER, params, new DataCallback<Order>() {
            @Override
            public void Failed(Exception e) {
                Log.e(TAG, "获取代工产品失败：" + e.getMessage());
                mPageNumber--;
                mplv.onRefreshComplete();
            }

            @Override
            public void Success(List<Order> datas) {
                if (LOAD == flag)
                    mOrders.clear();
                mOrders.addAll(datas);
                if (mplv.isRefreshing())//关闭刷新
                    mplv.onRefreshComplete();
                if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
                    mLoading.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initView() {
        mplv = (PullToRefreshListView) findViewById(R.id.list);
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mplv.setMode(PullToRefreshBase.Mode.BOTH);
        mplv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber = 1;
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
                final Order order = mOrders.get(position - 1);
                startActivity(new Intent(OEMProductActivity.this, OEMDetailActivity.class)
                        .putExtra("id", order.getMade_id()));
            }
        });
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        if (UserHelper.getInstance().getUser() != null &&
                UserHelper.getInstance().getUser().getAccount_type().equals("1")) {
            mFab.setVisibility(View.VISIBLE);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("代工需求");
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setTitleTextColor(Color.parseColor("#014886"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (UserHelper.getInstance().isLogined()) {
                    if (UserHelper.getInstance().getUser().getAccount_type().equals("0")) {
                        showToastShort("只有企业用户可以发布代工产品！");
                        return;
                    }
                    startActivity(new Intent(this, OEMAddActivity.class));
                } else {
                    showToastShort("请先登录！");
                    startActivity(new Intent(this, LoginActivity.class).putExtra("flag", "back"));
                }
                break;
        }
    }
}
