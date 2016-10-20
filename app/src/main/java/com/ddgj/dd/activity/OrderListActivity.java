package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OrderAdapter;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.bean.ResponseInfo;
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

public class OrderListActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private ImageView mBack;
    private TextView mTvCity;
    private TextView mTvPrice;
    private PullToRefreshListView mplv;
    private TextView mTitle;
    private int classes;
    private String prices[];
    private PopupWindow pop;
    /**
     * 页码
     */
    private int mPageNumber = 1;
    /**
     * 数量
     */
    private int mPageSingle = 4;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;
    /**
     * 更新数据
     */
    private static final int UPDATE = 2;

    private List<Order> mOrders;

    private OrderAdapter mAdapter;
    private LinearLayout mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        classes = getIntent().getIntExtra("classes", -1);
        prices = getResources().getStringArray(R.array.price);
        initView();
        mOrders = new ArrayList<Order>();
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
        params.put("made_type_id", String.valueOf(classes));
        Log.i("lgst", String.valueOf(classes));
        OkHttpUtils.post().url(GET_ORDER_FOR_TYPE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mPageNumber--;//网络访问失败，页码下次不能加1 所以先减一
                mplv.onRefreshComplete();
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
                            mOrders.clear();
                        }
                        for (int i = 0; i < ja.length(); i++) {
                            String orderStr = ja.getJSONObject(i).toString();
                            Order order = new Gson().fromJson(orderStr, Order.class);
                            mOrders.add(order);
                        }
                        if (flag == LOAD) {
                            mAdapter = new OrderAdapter(mOrders);
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
        mTitle = (TextView) findViewById(R.id.title);
        mBack = (ImageView) findViewById(R.id.back);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mplv = (PullToRefreshListView) findViewById(R.id.list);
        mplv.setMode(PullToRefreshBase.Mode.BOTH);

        mBack.setOnClickListener(this);
        mTvCity.setOnClickListener(this);
        mTvPrice.setOnClickListener(this);

        mTitle.setText(getIntent().getStringExtra("title"));

        mLoading = (LinearLayout) findViewById(R.id.loading);

        mplv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber=1;
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
                OkHttpUtils.post().url(GET_ORDER_DETAILS).params(params).build().execute(new StringCallback() {
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
                            startActivity(new Intent(OrderListActivity.this, WebActivity.class)
                                    .putExtra("title", order.getMade_name())
                                    .putExtra("url", HOST + url));
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_city:
                startActivityForResult(new Intent(this, CitySelectorActivity.class), 1);
                overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                break;
            case R.id.tv_price:
                showPopwin();
                break;
        }
    }

    private void showPopwin() {
        ListView listView = (ListView) getLayoutInflater().inflate(R.layout.view_search_popwin, null,
                false);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return prices.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, null);
                ((TextView) convertView).setText(prices[position]);
                return convertView;
            }
        });
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        pop = new PopupWindow(listView, 240, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 点击其他地方消失
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTvPrice.setText(((TextView) view).getText());
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                    pop = null;
                }
            }
        });
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_search_popwin_bg));
        pop.showAsDropDown(mTvPrice);
    }

    public void backClick(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SUCCESS == resultCode) {
            String city = data.getStringExtra("city");
            mTvCity.setText(city);
        }
    }
}
