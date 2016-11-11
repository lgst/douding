package com.ddgj.dd.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

public class OEMFactoryActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private ImageView mBack;
    private TextView mTitle;
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
        params.put("modify_differentiate", getIntent().getStringExtra("classes"));

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

//        OkHttpUtils.post().url(GET_ORDER_FACTORY).params(params).build().execute(new StringCallback() {
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
//                            mFactorys.clear();
////                            Log.i("lgst","CLEAR");
//                        }
//                        for (int i = 0; i < ja.length(); i++) {
//                            String str = ja.getJSONObject(i).toString();
//                            Log.i("lgst", str);
//                            EnterpriseUser factory = new Gson().fromJson(str, EnterpriseUser.class);
////                            factory.setHead_picture(new JSONObject(str).getString("facilitator_head"));
//                            mFactorys.add(factory);
//                        }
////                        Log.i("lgst", "==" + mOriginalitys.size());
//                        if (flag == LOAD) {
////                            Log.i("lgst","LOAD");
//                            mAdapter = new OrderFactoryAdapter(mFactorys);
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
        if (getIntent().getStringExtra("classes").equals("1")) {
            mTitle.setText("代工工厂");
        } else {
            mTitle.setText("订制工厂");
        }
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
                mHttpHelper.startDetailsPage(GET_ORDER_FACTORY_DETAILS,params,user);
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
//                            startActivity(new Intent(OEMFactoryActivity.this, WebActivity.class)
//                                    .putExtra("title", user.getFacilitator_name())
//                                    .putExtra("url", HOST + url)
//                                    .putExtra("account", user.getAccount())
//                                    .putExtra("content", ""));
//                        }
//                    }
//                });
            }
        });
        mplv.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
