package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OriginalityPLVAdapter;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
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

public class OriginalityActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, NetWorkInterface {

    private PullToRefreshListView mplv;
    private List<Originality> mOriginalitys;
    private OriginalityPLVAdapter mAdapter;
    private RadioGroup mRg;
    private LinearLayout mLoading;
    private TextView content;
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
    /**
     * 分类标志
     */
    private int classes = ALL;

    /**
     * 全部
     */
    private static final int ALL = 10;
    /**
     * 我的
     */
    private static final int NEW = 11;
    /**
     * 热门
     */
    private static final int HOT = 12;
    /**
     * 我的
     */
    private static final int MINE = 13;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_originality);
        mOriginalitys = new ArrayList<Originality>();
        initView();
        initDatas(LOAD, classes);
    }

    /**
     * @param: flag：数据加载方式  LOAD：重新加载  UPDATE：加载更多
     * classes:分类  ALL：全部   NEW：最新   HOT：最热   MINE：我的
     */
    private void initDatas(final int flag, final int classes) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
        if (classes == MINE) {
            params.put("o_account_id", UserHelper.getInstance().getUser().getAccount_id());
        } else {
            params.put("originality_differentiate", String.valueOf(0));
        }


//        Log.i("lgst", "mPageNumber:" + mPageNumber);
//        Log.i("lgst", "mPageSingle:" + mPageSingle);
        OkHttpUtils.post().url(getUrl(classes)).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                Log.e("lgst", e.getMessage() + " id:" + id);
                mPageNumber--;
                mplv.onRefreshComplete();
                showToastNotNetWork();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", response);
                try {
                    JSONObject jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (status == STATUS_SUCCESS) {
                        JSONArray ja = jo.getJSONArray("data");
//                        Log.i("lgst", jo.getString("msg") + "------" + classes);
                        if (flag == LOAD) {
                            mOriginalitys.clear();
//                            Log.i("lgst","CLEAR");
                        }
                        for (int i = 0; i < ja.length(); i++) {
                            String patentStr = ja.getJSONObject(i).toString();
//                            Log.i("lgst",patentStr);
                            Originality originality = new Gson().fromJson(patentStr, Originality.class);
                            if (classes == MINE)
                                originality.setHead_picture(UserHelper.getInstance().getUser().getHead_picture());
                            mOriginalitys.add(originality);
                        }
//                        Log.i("lgst", "==" + mOriginalitys.size());
                        if (flag == LOAD) {
//                            Log.i("lgst","LOAD");
                            mAdapter = new OriginalityPLVAdapter(OriginalityActivity.this, mOriginalitys);
                            mplv.setAdapter(mAdapter);
                        } else {
//                            Log.i("lgst","UPDATE");
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

    private String getUrl(int classes) {
        switch (classes) {
            case ALL:
                return GET_ALL_ORIGINALITY;
            case NEW:
                return GET_NEW_ORIGINALITY;
            case HOT:
                return GET_HOT_ORIGINALITY;
            case MINE:
                return GET_MINE_ORIGINALITY;
        }
        return GET_ALL_ORIGINALITY;
    }

    @Override
    public void initView() {
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mRg = (RadioGroup) findViewById(R.id.rg);
        mplv = (PullToRefreshListView) findViewById(R.id.plv);
        mplv.setMode(PullToRefreshBase.Mode.BOTH);
        mplv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber = 1;
                initDatas(LOAD, classes);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                initDatas(UPDATE, classes);
            }
        });
        mplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Originality originality = mOriginalitys.get(position - 1);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("originality_id", originality.getOriginality_id());
                OkHttpUtils.post().url(GET_ORIGINALITY_DETAILS).params(params).build().execute(new StringCallback() {
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
                            startActivity(new Intent(OriginalityActivity.this, WebActivity.class)
                                    .putExtra("title", originality.getOriginality_name())
                                    .putExtra("url", HOST + url)
                                    .putExtra("account", originality.getAccount())
                                    .putExtra("content", originality.getOriginality_details())
                                    .putExtra("id",originality.getOriginality_id())
                                    .putExtra("classes", 0));
                        }
                    }
                });
            }
        });
        mRg.setOnCheckedChangeListener(this);
        content = (TextView) findViewById(R.id.search_edit_text);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getInstance().isLogined()) {
                    startActivity(new Intent(OriginalityActivity.this, PublishCreativeActivity.class));
                } else {
                    showToastShort("请先登录！");
                    startActivity(new Intent(OriginalityActivity.this, LoginActivity.class).putExtra("flag", "back"));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {
            String text = data.getStringExtra("content");
            content.setText(text);

        }
    }

    public void backClick(View v) {
        finish();
    }

    public void searchClick(View v) {
        startActivityForResult(new Intent(this, SearchActivity.class).putExtra("content", "创意"), 1);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mLoading.getVisibility() == View.VISIBLE) {
            return;
        }
        switch (checkedId) {
            case R.id.rb_all://全部
                changClasses(ALL);
                break;
            case R.id.rb_new://最新
                changClasses(NEW);
                break;
            case R.id.rb_hot://热门
                changClasses(HOT);
                break;
            case R.id.rb_mine://我的
                if (UserHelper.getInstance().isLogined()) {
                    changClasses(MINE);
                } else {
                    startActivity(new Intent(this, LoginActivity.class).putExtra("flag", LoginActivity.BACK));
                    ((RadioButton) mRg.getChildAt(0)).setChecked(true);
                }
                break;
        }
    }

    private void changClasses(int classes) {
        this.classes = classes;
        mLoading.setVisibility(View.VISIBLE);
        mOriginalitys.clear();
        mAdapter.notifyDataSetChanged();
        mPageNumber = 1;
        initDatas(LOAD, classes);
    }
}
