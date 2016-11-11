package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

    private ImageView mBack;
    private TextView mTitle;
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

//        OkHttpUtils.post().url(GET_ORDER).addParams("made_state", "2")//后台初始值位0会查不出来数据
//                .addParams("made_differentiate", "1")
//                .addParams("pageNumber", String.valueOf(mPageNumber))
//                .addParams("pageSingle", String.valueOf(mPageSingle))
//                .build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
////                Log.e("lgst", e.getMessage() + " id:" + id);
//                mPageNumber--;
//                mplv.onRefreshComplete();
//                showToastNotNetWork();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                Log.i("lgst", response);
//                try {
//                    JSONObject jo = new JSONObject(response);
//                    int status = jo.getInt("status");
//                    if (status == STATUS_SUCCESS) {
//                        JSONArray ja = jo.getJSONArray("data");
////                        Log.i("lgst", jo.getString("msg") + "------" + classes);
//                        if (flag == LOAD) {
//                            mOrders.clear();
////                            Log.i("lgst","CLEAR");
//                        }
//                        for (int i = 0; i < ja.length(); i++) {
//                            String str = ja.getJSONObject(i).toString();
//                            Log.i("lgst", str);
//                            Order order = new Gson().fromJson(str, Order.class);
//                            mOrders.add(order);
//                        }
////                        Log.i("lgst", "==" + mOriginalitys.size());
//                        if (flag == LOAD) {
////                            Log.i("lgst","LOAD");
//                            mAdapter = new OrderAdapter(mOrders);
//                            mplv.setAdapter(mAdapter);
//                        } else {
////                            Log.i("lgst","UPDATE");
//                            if (mAdapter != null)
//                                mAdapter.notifyDataSetChanged();
//                        }
////                        if (i < mPageSingle)//如果返回数据小于请求数量则表示已经取到最后一条数据，页码就不能再加一，每次请求前页码加一，所以这里要减一
////                            mPageNumber--;
//                        if (mplv.isRefreshing())//关闭刷新
//                            mplv.onRefreshComplete();
//                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
//                            mLoading.setVisibility(View.GONE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }

    @Override
    public void initView() {

        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("made_id", order.getMade_id());
                mOrderHttpHelper.startDetailsPage(GET_ORDER_PRODUCT_DETAILS,params,order);
//                OkHttpUtils.post().url(GET_ORDER_PRODUCT_DETAILS).params(params).build().execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Log.e("lgst", "获取代工产品详情页失败：" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        Log.i("lgst", response);
//                        ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
//                        if (responseInfo.getStatus() == STATUS_SUCCESS) {
//                            String url = responseInfo.getData();
//                            Log.e("lgst", url);
//                            startActivity(new Intent(OEMProductActivity.this, WebActivity.class)
//                                    .putExtra("title", order.getMade_name())
//                                    .putExtra("url", HOST + url)
//                                    .putExtra("account", order.getAccount())
//                                    .putExtra("content", order.getMade_describe()));
//                        }
//                    }
//                });
            }
        });
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        if (UserHelper.getInstance().getUser() != null &&
                UserHelper.getInstance().getUser().getAccount_type().equals("1")) {
            mFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
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
