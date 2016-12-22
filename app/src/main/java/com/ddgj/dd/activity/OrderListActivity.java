package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OrderAdapter;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订制二级页面
 */
public class OrderListActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

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
    private int mPageSingle = 15;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;
    /**
     * 更新数据
     */
    private static final int UPDATE = 2;

    private List<Order> mOrders = new ArrayList<Order>();

    private OrderAdapter mAdapter = new OrderAdapter(mOrders);
    private LinearLayout mLoading;

    private String mAddr;
    private HttpHelper<Order> mHttpHelper;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        classes = getIntent().getIntExtra("classes", -1);
        prices = getResources().getStringArray(R.array.price);
        initView();
        initData(LOAD);
    }


    private void initData(final int flag) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        String price = mTvPrice.getText().toString();
        if (!price.equals("全部价格")) {
            params.put("made_price", price);
        }
        mAddr = mTvCity.getText().toString();
        if (!mAddr.equals("全国"))
            params.put("city", mAddr);
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
        params.put("made_type_id", String.valueOf(classes));
        mHttpHelper = new HttpHelper<Order>(this, Order.class);
        mHttpHelper.getDatasPost(GET_ORDER_FOR_TYPE, params, new DataCallback<Order>() {
            @Override
            public void Failed(Exception e) {
                mPageNumber--;//网络访问失败，页码下次不能加1 所以先减一
                mplv.onRefreshComplete();
            }

            @Override
            public void Success(List<Order> datas) {
                if (flag == LOAD)
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
        mTitle = (TextView) findViewById(R.id.title);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mplv = (PullToRefreshListView) findViewById(R.id.list);
        mplv.setMode(PullToRefreshBase.Mode.BOTH);
        mAddr = getSharedPreferences("location", MODE_PRIVATE).getString("city", "全国");
        mTvCity.setText(mAddr);
        mTvCity.setOnClickListener(this);
        mTvPrice.setOnClickListener(this);
        mTitle.setText(getIntent().getStringExtra("title"));
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mplv.setAdapter(mAdapter);
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
                startActivity(new Intent(OrderListActivity.this, OrderDetailActivity.class)
                        .putExtra("id", order.getMade_id()));
            }
        });
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
            case R.id.tv_city:
                startActivityForResult(new Intent(this, CitySelecterActivity.class), 1);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
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
        int screenWhite = getWindowManager().getDefaultDisplay().getWidth();
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        pop = new PopupWindow(listView, screenWhite / 2, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 点击其他地方消失
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTvPrice.setText(((TextView) view).getText());
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                    pop = null;
                    initData(LOAD);
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
            initData(LOAD);
        }
    }
}