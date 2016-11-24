package com.ddgj.dd.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ddgj.dd.R;
import com.ddgj.dd.adapter.FavoriteAdapter;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.FavoriteInfo;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
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

import static com.ddgj.dd.util.net.NetWorkInterface.GET_ORDER_FACTORY_DETAILS;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_ORDER_PRODUCT_DETAILS;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_ORIGINALITY_DETAILS;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_PATENT_DETAILS;

public class FavoriteActivity extends BaseActivity {

    private Toolbar mToolbar;
    private SwipeMenuListView mListView;
    private List<FavoriteInfo> mFavorites = new ArrayList<FavoriteInfo>();
    private FavoriteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initView();
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("pageNumber", "1");
        params.put("pageSingle", "1000");
        OkHttpUtils.post().params(params).url(NetWorkInterface.GET_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "查询我的收藏失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String json = ja.getJSONObject(i).toString();
                            FavoriteInfo info = new Gson().fromJson(json, FavoriteInfo.class);
                            mFavorites.add(info);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView = (SwipeMenuListView) findViewById(R.id.list_view);
        mFavorites = new ArrayList<FavoriteInfo>();
        mAdapter = new FavoriteAdapter(mFavorites);
        mListView.setAdapter(mAdapter);
        mListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(FavoriteActivity.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.grey_lightE)));
                // set item width
                deleteItem.setWidth(DensityUtil.dip2px(FavoriteActivity.this, 90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete_grey600_48dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        });
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteData(position);
                        break;
                }
                return false;
            }
        });
       // View emptyView = getLayoutInflater().inflate(R.layout.item_not_data, (ViewGroup) mListView.getParent());
       // ((ViewGroup)mListView.getParent()).addView(emptyView);
        mListView.setEmptyView(findViewById(R.id.not_data));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavoriteInfo info = mFavorites.get(position);
                /*0为专利 1为创意 2私人订制 3为代工产品 4为订制厂家 5 为代工厂家 6为中国智造企业 7为帖子*/
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                switch (Integer.parseInt(info.getCollection_type())) {
                    case 0:
                        Patent patent = new Patent();
                        patent.setAccount(info.getC_u_account());
                        patent.setPatent_id(info.getC_from_id());
                        patent.setPatent_name(info.getC_from_title());
                        patent.setPatent_details("");//详情在分享中会用到
                        params.put("patent_id", patent.getPatent_id());
                        new HttpHelper<Patent>(FavoriteActivity.this, Patent.class).startDetailsPage(GET_PATENT_DETAILS, params, patent);
                        break;
                    case 1:
                        params.put("client_side", "app");
                        Originality originality = new Originality();
                        originality.setAccount(info.getC_u_account());
                        originality.setOriginality_id(info.getC_from_id());
                        originality.setOriginality_name(info.getC_from_title());
                        originality.setOriginality_details("");//详情在分享中会用到
                        params.put("originality_id", originality.getOriginality_id());
                        new HttpHelper<Originality>(FavoriteActivity.this, Originality.class).startDetailsPage(GET_ORIGINALITY_DETAILS, params, originality);
                        break;
                    case 2:
                        startOrderDetailPage(params,info,"0");
                        break;
                    case 3:
                        startOrderDetailPage(params,info,"1");
                        break;
                    case 4: {
                        startEnterpriseDetailPage(params,info,"4");
                        break;
                    }
                    case 5: {
                        startEnterpriseDetailPage(params,info,"5");
                        break;
                    }
                    case 6: {
                        startEnterpriseDetailPage(params,info,"6");
                        break;
                    }
                    case 7:
                        break;
                }
            }
        });
    }

    private void startEnterpriseDetailPage(Map<String,String> params,FavoriteInfo info,String classes) {
        params.put("client_side", "app");
        EnterpriseUser user = new EnterpriseUser();
        user.setAccount(info.getC_u_account());
        user.setAccount_id(info.getC_from_id());
        user.setFacilitator_name(info.getC_from_title());
        user.setModify_differentiate(classes);
        params.put("acilitator_id", user.getAccount_id());
        new HttpHelper<EnterpriseUser>(FavoriteActivity.this, EnterpriseUser.class)
                .startDetailsPage(GET_ORDER_FACTORY_DETAILS, params, user);
    }

    /**
     * 订制和代工详情页跳转
     * @param params
     * @param info
     * @param classes 订制：0，代工产品：1
     */
    private void startOrderDetailPage(Map<String,String> params,FavoriteInfo info,String classes) {
        params.put("client_side", "app");
        Order order = new Order();
        order.setAccount(info.getC_u_account());
        order.setMade_id(info.getC_from_id());
        order.setMade_name(info.getC_from_title());
        order.setMade_describe("");//详情
        order.setMade_differentiate(classes);//代工产品
        params.put("made_id", order.getMade_id());
        new HttpHelper<Order>(FavoriteActivity.this, Order.class).startDetailsPage(GET_ORDER_PRODUCT_DETAILS, params, order);
    }

    private void deleteData(final int position) {
        FavoriteInfo favorite = mFavorites.get(position);
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_id", favorite.getCollection_id());
        OkHttpUtils.post().params(params).url(NetWorkInterface.DELETE_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "收藏删除失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "删除收藏成功: " + response);
                mFavorites.remove(position);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(FavoriteActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
