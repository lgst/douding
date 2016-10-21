package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.FactoryAdapter;
import com.ddgj.dd.adapter.FactoryProductsAdapter;

import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.Originality;
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

import static com.ddgj.dd.util.net.NetWorkInterface.GET_FACILITATORMADE;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_PRODUCTMADE;
import static com.ddgj.dd.util.net.NetWorkInterface.HOST;
import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;


/**
 * Created by Administrator on 2016/10/13/0013.
 */

public class FactoryActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private LinearLayout floading;
    private RadioGroup fRG;
    private RadioButton enterprise;//企业
    private RadioButton product;//产品
    private FactoryAdapter factoryAdapter;
    private FactoryProductsAdapter factoryProductsAdapter;
    /**
     * 企业
     */
    private List<EnterpriseUser> facilitator;
    /**
     * 产品
     */
    private List<Originality> facProOriginality;
    private PullToRefreshListView factorylist;


    /**
     * 商家分类
     * 服务商区分 0为一般商家 1为代工商家 2为订制商家
     */
    private int fmodify_differentiate = 0;

    /**
     * 产品
     * 创意区分 0为个人创意 1为商家创意产品
     */
    private int foriginality_differentiate = 1;

    /**
     * 页码
     */
    private int fPageNumber = 1;

    /**
     * 数量
     */
    private int fPageSingle = 4;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;

    /**
     * 更新数据
     */
    private static final int UPDATE = 2;
    /**
     * 分类
     */
    private int classes = FAC;
    /**
     * 企业
     */
    private static final int FAC = 10;

    /**
     * 产品
     */
    private static final int PRODUCT = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        initView();
        facilitator = new ArrayList<EnterpriseUser>();
        facProOriginality = new ArrayList<Originality>();
        initDatas(LOAD, classes);

    }

    private void initDatas(final int flag, final int classes) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        if (classes == PRODUCT) {
            params.put("originality_differentiate", String.valueOf(foriginality_differentiate));
        } else {
            params.put("modify_differentiate", String.valueOf(fmodify_differentiate));
        }
        params.put("pageNumber", String.valueOf(fPageNumber));
        params.put("pageSingle", String.valueOf(fPageSingle));
        OkHttpUtils.post().url(getUrl(classes)).params(params).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                fPageNumber--;
                factorylist.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject fjs = new JSONObject(response);
                    int status = fjs.getInt("status");
                    if (status == STATUS_SUCCESS) {
                        JSONArray ja = fjs.getJSONArray("data");

                        if (flag == LOAD) {
                            if (classes == FAC) {
                                facilitator.clear();
                            } else {
                                facProOriginality.clear();
                            }
                        }
                        if (classes == FAC) {
                            for (int i = 0; i < ja.length(); i++) {
                                String facStr = ja.getJSONObject(i).toString();
                                EnterpriseUser facilit = new Gson().fromJson(facStr, EnterpriseUser.class);
                                facilitator.add(facilit);
                            }
                        } else {
                            for (int i = 0; i < ja.length(); i++) {
                                String facStr = ja.getJSONObject(i).toString();
                                Originality facilit = new Gson().fromJson(facStr, Originality.class);
                                facProOriginality.add(facilit);
                            }
                        }

                        if (flag == LOAD) {
                            if (classes == FAC) {
                                factoryAdapter = new FactoryAdapter(FactoryActivity.this, facilitator);
                                factorylist.setAdapter(factoryAdapter);
                            } else {
                                factoryProductsAdapter = new FactoryProductsAdapter(FactoryActivity.this, facProOriginality);
                                factorylist.setAdapter(factoryProductsAdapter);
                            }
                        } else {
                            if (classes == FAC) {
                                if (factoryAdapter != null)
                                    factoryAdapter.notifyDataSetChanged();
                            } else {
                                if (factoryProductsAdapter != null)
                                    factoryProductsAdapter.notifyDataSetChanged();
                            }
                        }

                        if (factorylist.isRefreshing())
                            factorylist.onRefreshComplete();
                        if (floading.getVisibility() == View.VISIBLE)
                            floading.setVisibility(View.GONE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String getUrl(int classes) {
        switch (classes) {
            case FAC:
                return GET_FACILITATORMADE;
            case PRODUCT:
                return GET_PRODUCTMADE;

        }

        return GET_FACILITATORMADE;
    }


    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        floading = (LinearLayout) this.findViewById(R.id.loading);
        fRG = (RadioGroup) this.findViewById(R.id.rg_factory);
        factorylist = (PullToRefreshListView) this.findViewById(R.id.lv_factory);
        factorylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (classes == FAC) {
                    String id = facilitator.get(i - 1).getAccount_id();
                    final String title = facilitator.get(i - 1).getFacilitator_name();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("acilitator_id", id);
                    params.put("client_side", "app");
                    OkHttpUtils.post().url(NetWorkInterface.GET_FAC_DETAILS).params(params).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e("Error", e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                            String url = responseInfo.getData();
                            startActivity(new Intent(FactoryActivity.this, WebActivity.class)
                                    .putExtra("url", HOST + "/" + url)
                                    .putExtra("title", title));

                        }
                    });
                } else {
                    String id = facProOriginality.get(i-1).getOriginality_id();
                    final  String title = facProOriginality.get(i-1).getOriginality_name();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("client_side", "app");
                    params.put("originality_id", id);
                    OkHttpUtils.post().url(NetWorkInterface.GET__PRODUCT_DETAILS).params(params).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e("Error", e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                            String url = responseInfo.getData();
                            startActivity(new Intent(FactoryActivity.this, WebActivity.class )
                                    .putExtra("url", HOST + "/" + url)
                                    .putExtra("title", title));

                        }
                    });
                }

            }
        });

        /**
         * 上拉刷新工厂
         * 下拉加载工厂
         */
        factorylist.setMode(PullToRefreshBase.Mode.BOTH);
        factorylist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fPageNumber = 1;
                initDatas(LOAD, classes);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fPageNumber++;//加载更多，页码加一
                initDatas(UPDATE, classes);
            }
        });

        fRG.setOnCheckedChangeListener(this);
    }

    public void backClick(View v) {
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (floading.getVisibility() == View.VISIBLE) {
            return;
        }
        switch (checkedId) {
            case R.id.rb_factory_enterprise:
                change(FAC);
                break;
            case R.id.rb_factory_product:
                change(PRODUCT);
                break;
        }
    }

    private void change(int classes) {
        this.classes = classes;
        floading.setVisibility(View.VISIBLE);
        if (classes == FAC) {
            facilitator.clear();
            factoryAdapter.notifyDataSetChanged();
        } else {
            facProOriginality.clear();
            if (factoryProductsAdapter != null)
                factoryProductsAdapter.notifyDataSetChanged();
        }
        fPageNumber = 1;
        initDatas(LOAD, classes);
    }

}
