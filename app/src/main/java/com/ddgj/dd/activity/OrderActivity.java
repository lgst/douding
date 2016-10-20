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
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;
import com.google.gson.Gson;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        initDatas();
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
                        startActivity(new Intent(OrderActivity.this,OrderListActivity.class).putExtra("title",names[0]).putExtra("classes",1));
                        break;
                    case 1://服装
                        startActivity(new Intent(OrderActivity.this,OrderListActivity.class).putExtra("title",names[1]).putExtra("classes",2));
                        break;
                    case 2://礼品
                        startActivity(new Intent(OrderActivity.this,OrderListActivity.class).putExtra("title",names[2]).putExtra("classes",3));
                        break;
                    case 3://机械
                        startActivity(new Intent(OrderActivity.this,OrderListActivity.class).putExtra("title",names[3]).putExtra("classes",4));
                        break;
                    case 4://电子
                        startActivity(new Intent(OrderActivity.this,OrderListActivity.class).putExtra("title",names[4]).putExtra("classes",5));
                        break;
                    case 5://其他
                        startActivity(new Intent(OrderActivity.this,OrderListActivity.class).putExtra("title",names[5]).putExtra("classes",6));
                        break;
                    case 6://工厂
                        startActivity(new Intent(OrderActivity.this, OrderFactoryActivity.class).putExtra("classes","2"));
                        break;
                    case 7://发布
//                        startActivity(new Intent(OrderActivity.this, OrderAddActivity.class));
                        break;
                }
            }
        });
    }

    public void backClick(View v) {
        finish();
    }

    private void initDatas(){
        //获取订制
        OkHttpUtils.post().url(GET_ORDER).addParams("made_state","2")
                .addParams("made_differentiate","0")
                .addParams("pageNumber","1")
                .addParams("pageSingle","5")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lgst","获取订制成功案例失败："+e.getMessage());
                        showToastShort("获取成功案例出错！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("lgst",response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            if (status == STATUS_SUCCESS) {
                                mOrders = new ArrayList<Order>();
                                JSONArray ja = jo.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    String orderStr = ja.getJSONObject(i).toString();
                                    Order order = new Gson().fromJson(orderStr, Order.class);
                                    mOrders.add(order);
                                }
                                mSuccess.setAdapter(new OrderAdapter(mOrders));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mSuccess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Order order = mOrders.get(position);
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
                            startActivity(new Intent(OrderActivity.this, WebActivity.class)
                                    .putExtra("title", order.getMade_name())
                                    .putExtra("url", HOST + url));
                        }
                    }
                });
            }
        });
    }
}
