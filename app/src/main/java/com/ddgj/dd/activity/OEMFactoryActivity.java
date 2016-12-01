package com.ddgj.dd.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OrderFactoryAdapter;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OEMFactoryActivity extends BaseActivity implements NetWorkInterface {

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
    private int mPageSingle = 10;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;
    /**
     * 更新数据
     */
    private static final int UPDATE = 2;
    private OrderFactoryAdapter mAdapter;
    private HttpHelper<EnterpriseUser> mHttpHelper;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_factory);
        mAdapter = new OrderFactoryAdapter(mFactorys);
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
        params.put("modify_differentiate", "1");

        mHttpHelper = new HttpHelper<EnterpriseUser>(this, EnterpriseUser.class);
        mHttpHelper.getDatasPost(GET_ORDER_FACTORY, params, new DataCallback<EnterpriseUser>() {
            @Override
            public void Failed(Exception e) {
                mPageNumber--;
                mplv.onRefreshComplete();
                showToastNotNetWork();
            }

            @Override
            public void Success(List<EnterpriseUser> datas) {
                if (flag == LOAD)
                    mFactorys.clear();
                mFactorys.addAll(datas);
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("代工工厂");
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
                final EnterpriseUser user = mFactorys.get(position - 1);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("acilitator_id", user.getAccount_id());
                mHttpHelper.startDetailsPage(GET_ORDER_FACTORY_DETAILS, params, user);
            }
        });
        mplv.setAdapter(mAdapter);
    }
}
