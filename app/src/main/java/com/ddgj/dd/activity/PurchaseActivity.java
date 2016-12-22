package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.PurchaseAdapter;
import com.ddgj.dd.bean.Purchase;
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

/**
 * @author QinDaluzy
 *         采购列表页
 */

public class PurchaseActivity extends BaseActivity implements NetWorkInterface, AdapterView.OnItemClickListener, View.OnClickListener {
    private TextView pucCity;

    private List<Purchase> mPurchaseList = new ArrayList<Purchase>();
    private PurchaseAdapter mAdapter;

    private PullToRefreshListView mList;
    private int pageSingle = 10;
    private int pageNumber = 1;
    private static final int UPDATE = 1;
    private static final int LOAD = 2;
    private String city = "";
    private String area = "";
    private String serach = "";
    private Spinner spinner_type, spinner_time;
    private FloatingActionButton btn_publish;
    private Toolbar mToolbar;
    private AppCompatTextView mSearch;
   /* private FloatingActionButton purfloatingactionbutton;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        initView();
        mList.setRefreshing(true);
        initData(LOAD);
    }

    public void initView() {
        btn_publish = (FloatingActionButton) this.findViewById(R.id.purchase_publish);
        btn_publish.setOnClickListener(this);
        spinner_type = (Spinner) this.findViewById(R.id.spinner_purchase_type);
        spinner_time = (Spinner) this.findViewById(R.id.spinner_purchasr_time);
        mList = (PullToRefreshListView) findViewById(R.id.plv);
        mList.setMode(PullToRefreshBase.Mode.BOTH);
        mList.setOnItemClickListener(this);

        city = getSharedPreferences("location", MODE_PRIVATE).getString("city", "");
        pucCity = (TextView) this.findViewById(R.id.purchase_user_city);
        pucCity.setText(city);
        mList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNumber = 1;
                initData(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNumber++;
                initData(UPDATE);
            }
        });
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("采购");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearch = (AppCompatTextView) findViewById(R.id.search);
        mSearch.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.purchase_publish:
                Intent intent = new Intent();
                intent.setClass(PurchaseActivity.this, PublishPurchaseActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                startActivityForResult(new Intent(this, SearchActivity.class).putExtra("title", "悬赏"), 2);
                break;
        }
    }

    public void addrClick(View v) {
        startActivityForResult(new Intent(this, CitySelecterActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS && requestCode == 1) {
            String city = data.getStringExtra("city");
            pucCity.setText(city);
        }
        if (resultCode == SUCCESS && requestCode == 2) {
            mSearch.setText(data.getStringExtra("content"));
            initData(LOAD);
        }
    }

    private void initData(final int flag) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("procurement_type", "0");
        params.put("procurement_classes", String.valueOf(spinner_type.getSelectedItem()));
        params.put("procurement_province", "");
        params.put("procurement_city", city);
        params.put("procurement_area", area);
        params.put("procurement_name", serach);
        params.put("pageSingle", String.valueOf(pageSingle));
        params.put("pageNumber", String.valueOf(pageNumber));
        OkHttpUtils.post().url(GET_PROCUREMENT).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(PurchaseActivity.this, "请求失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                mList.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        if (flag == LOAD)
                            mPurchaseList.clear();
                        for (int i = 0; i < ja.length(); i++) {
                            Purchase purchase = new Gson().fromJson(ja.getString(i), Purchase.class);
                            mPurchaseList.add(purchase);
                        }
                        if (flag == LOAD) {
                            mAdapter = new PurchaseAdapter(PurchaseActivity.this, mPurchaseList);
                            mList.setAdapter(mAdapter);
                        } else {
                            if (mAdapter != null) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    mList.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String purchaseId = mPurchaseList.get(position - 1).getProcurement_id();
        Intent intent = new Intent();
        intent.setClass(PurchaseActivity.this, DetailsPurchaseActivity.class);
        intent.putExtra("purchaseId", purchaseId);
        startActivity(intent);
    }
}
