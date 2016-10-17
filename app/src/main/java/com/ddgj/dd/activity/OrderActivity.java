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
import java.util.List;

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
    /**
     * 推荐
     */
    private CustomListView recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initViews();
        initDatas();
    }

    @Override
    public void initViews() {
        recommend = (CustomListView) findViewById(R.id.recommend);
        mSuccess = (CustomListView) findViewById(R.id.success);
        classes = (CustomGridView) findViewById(R.id.classes);
        classes.setAdapter(new OrderClassesAdapter(this));
        classes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://家具

                        break;
                    case 1://服装

                        break;
                    case 2://礼品

                        break;
                    case 3://机械

                        break;
                    case 4://电子

                        break;
                    case 5://其他

                        break;
                    case 6://工厂

                        break;
                    case 7://发布
                        startActivity(new Intent(OrderActivity.this, OrderAddActivity.class));
                        break;
                }
            }
        });
    }

    public void backClick(View v) {
        finish();
    }

    private void initDatas(){
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
                                List<Order> orders = new ArrayList<Order>();
                                JSONArray ja = jo.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    String orderStr = ja.getJSONObject(i).toString();
                                    Order order = new Gson().fromJson(orderStr, Order.class);
                                    orders.add(order);
                                }
                                mSuccess.setAdapter(new OrderAdapter(orders));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
