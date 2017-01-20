package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.user.UserHelper;
import com.lidroid.xutils.DbUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_MINE_ORDER;

public class MineCustomActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private List<Order> mOrders = new ArrayList<Order>();
    private int pageNumber = 0;
    private ListView mListView;
    private View mView;
    private LinearLayout mLoading;
    private boolean refresh;
    private MlvAdapter mAdapter;
    //    0为等待接单 1为已接单 2为成功 3为失败 4服务方申请合作 5服务方申请验收
    private static final String[] STATUS = {"等待接单", "工作中", "交易成功", "交易失败", "待确认合作", "待验收"};
    private int[] colors;
    private DbUtils mDbu;
    private boolean noMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_custom);
        mDbu = DbUtils.create(getApplicationContext(), StringUtils.getDbName());
        initView();
        colors = new int[]{R.color.waiting,
                R.color.working,
                R.color.finished,
                R.color.grey,
                R.color.colorPrimary,
                R.color.blue};
        initData();
    }

    private void initData() {
        if (noMore)
            return;
//        try {
//            List<Order> orders = mDbu.findAll(Selector.from(Order.class).limit(10).offset(pageNumber));
//            if (orders != null) {
//                mOrders.addAll(orders);
//                mAdapter.notifyDataSetChanged();
//            }
//            if (orders == null || orders.size() < 10) {
//                mListView.removeFooterView(mView);
//                noMore = true;
//            }
//            mLoading.setVisibility(View.GONE);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(pageNumber));
        params.put("pageSingle", "10");
        params.put("made_differentiate", "0");
        params.put("m_a_id", UserHelper.getInstance().getUser().getAccount_id());
        new HttpHelper<Order>(this, Order.class)
                .getDatasPost(GET_MINE_ORDER, params, new DataCallback<Order>() {
                    @Override
                    public void Failed(Exception e) {
                        Log.e("lgst", "我的订制获取出错：" + e.getMessage());
                    }

                    @Override
                    public void Success(List<Order> datas) {
                        if (datas.size() < 10)
                            mListView.removeFooterView(mView);
                        mOrders.addAll(datas);
                        mAdapter.notifyDataSetChanged();
                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
                            mLoading.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("我的订制");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE)
                    refresh = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && refresh) {
                    //加载数据
                    pageNumber++;
                    initData();
                    refresh = false;
                }
            }
        });
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mView = getLayoutInflater().inflate(R.layout.item_listview_footer, null);
        mListView.addFooterView(mView);
        mAdapter = new MlvAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(findViewById(R.id.not_data));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Order order = mOrders.get(position);
        startActivity(new Intent(this, MineOrdersDetailActivity.class)
                .putExtra("id", order.getMade_id()));
    }

    class MlvAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mOrders.size();
        }

        @Override
        public Object getItem(int position) {
            return mOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_mine_custom, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            Order order = mOrders.get(position);
            vh.tv_title.setText(order.getMade_title());
            vh.tv_content.setText(order.getMade_describe());
            vh.tv_date.setText(order.getMade_time());
            int status = Integer.parseInt(order.getMade_state());
            vh.tv_status.setText(STATUS[status]);
            vh.tv_status.setBackgroundColor(getResources().getColor(colors[status]));
            return convertView;
        }

        public class ViewHolder {
            View rootView;
            TextView tv_title;
            TextView tv_content;
            TextView tv_date;
            TextView tv_status;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.tv_title = (TextView) rootView.findViewById(R.id.tv_title);
                this.tv_content = (TextView) rootView.findViewById(R.id.tv_content);
                this.tv_date = (TextView) rootView.findViewById(R.id.tv_date);
                this.tv_status = (TextView) rootView.findViewById(R.id.tv_status);
            }

        }
    }
}
