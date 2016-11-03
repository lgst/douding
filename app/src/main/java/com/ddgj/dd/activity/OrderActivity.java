package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OrderAdapter;
import com.ddgj.dd.adapter.OrderClassesAdapter;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订制页面
 */
public class OrderActivity extends BaseActivity implements NetWorkInterface {
    /**
     * 分类
     */
    private CustomGridView classes;
    /**
     * 成功案例
     */
    private CustomListView mSuccess;
    private String[] names;
    private List<Order> mOrders;
    private HttpHelper<Order> mOrderHttpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        mOrderHttpHelper = new HttpHelper<Order>(this,Order.class);
        initView();
        initCache();
        initDatas();
    }

    private void initCache() {
        mOrders = mOrderHttpHelper.analysisAndLoadOriginality(FileUtil.readJsonFromCacha("order"));
        mSuccess.setAdapter(new OrderAdapter(mOrders));
    }

    @Override
    public void initView() {
        mSuccess = (CustomListView) findViewById(R.id.success);
        classes = (CustomGridView) findViewById(R.id.classes);
        classes.setAdapter(new OrderClassesAdapter(this));
        names = getResources().getStringArray(R.array.order_classes_name);
        classes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://家具
                        startActivity(new Intent(OrderActivity.this, OrderListActivity.class).putExtra("title", names[0]).putExtra("classes", 1));
                        break;
                    case 1://服装
                        startActivity(new Intent(OrderActivity.this, OrderListActivity.class).putExtra("title", names[1]).putExtra("classes", 2));
                        break;
                    case 2://礼品
                        startActivity(new Intent(OrderActivity.this, OrderListActivity.class).putExtra("title", names[2]).putExtra("classes", 3));
                        break;
                    case 3://机械
                        startActivity(new Intent(OrderActivity.this, OrderListActivity.class).putExtra("title", names[3]).putExtra("classes", 4));
                        break;
                    case 4://电子
                        startActivity(new Intent(OrderActivity.this, OrderListActivity.class).putExtra("title", names[4]).putExtra("classes", 5));
                        break;
                    case 5://其他
                        startActivity(new Intent(OrderActivity.this, OrderListActivity.class).putExtra("title", names[5]).putExtra("classes", 6));
                        break;
                    case 6://工厂
                        startActivity(new Intent(OrderActivity.this, OEMFactoryActivity.class).putExtra("classes", "2"));
                        break;
                    case 7://发布
                        if (UserHelper.getInstance().isLogined()) {
                            startActivity(new Intent(OrderActivity.this, OrderAddActivity.class));
                        } else {
                            showToastShort("请先登录！");
                            startActivity(new Intent(OrderActivity.this, LoginActivity.class).putExtra("flag", "back"));
                        }
                        break;
                }
            }
        });
    }

    public void backClick(View v) {
        finish();
    }

    private void initDatas() {
        //获取订制
        Map<String,String> params = new HashMap<String,String>();
        params.put("made_state", "2");
        params.put("made_differentiate", "0");
        params.put("pageNumber", "1");
        params.put("pageSingle", "5");
        mOrderHttpHelper.getDatasPost(GET_ORDER, params, new DataCallback<Order>() {
            @Override
            public void Failed(Exception e) {
                Log.e(TAG, "获取成功定制出错："+e.getMessage());
            }

            @Override
            public void Success(List<Order> datas) {
                mOrders=datas;
                mSuccess.setAdapter(new OrderAdapter(mOrders));
            }
        });
        mSuccess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Order order = mOrders.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("made_id", order.getMade_id());
                mOrderHttpHelper.startDetailsPage(GET_ORDER_DETAILS,params,order);
            }
        });
    }
}
