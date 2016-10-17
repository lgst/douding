package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.PatentPLVAdapter;
import com.ddgj.dd.bean.Patent;
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

import static com.ddgj.dd.R.id.floatingActionButton;

public class PatentActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, NetWorkInterface {

    private PullToRefreshListView mplv;
    private List<Patent> mPatents;
    private PatentPLVAdapter mAdapter;
    private RadioGroup mRg;
    private LinearLayout mLoading;
    private TextView content;
    private FloatingActionButton floatingActionButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patent);
        initViews();
        mPatents = new ArrayList<Patent>();
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
        if (classes == MINE)
            params.put("p_account_id", UserHelper.getInstance().getUser().getAccount_id());

        OkHttpUtils.post().url(getUrl(classes)).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mPageNumber--;//网络访问失败，页码下次不能加1 所以先减一
                mplv.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (status == STATUS_SUCCESS) {
                        JSONArray ja = jo.getJSONArray("data");
                        if (flag == LOAD) {
                            mPatents.clear();
                        }
                        for (int i = 0; i < ja.length(); i++) {
                            String patentStr = ja.getJSONObject(i).toString();
                            Patent patent = new Gson().fromJson(patentStr, Patent.class);
                            mPatents.add(patent);
                        }
                        if (flag == LOAD) {
                            mAdapter = new PatentPLVAdapter(PatentActivity.this, mPatents);
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

    private String getUrl(int classes) {
        switch (classes) {
            case ALL:
                return GET_ALL_PATENT;
            case NEW:
                return GET_NEW_PATENT;
            case HOT:
                return GET_HOT_PATENT;
            case MINE:
                return GET_MINE_PATENT;
        }
        return GET_ALL_PATENT;
    }

    @Override
    public void initViews() {
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
                mPageNumber++;//加载更多，页码加一
                initDatas(UPDATE, classes);
            }
        });
        mRg.setOnCheckedChangeListener(this);
        content = (TextView) findViewById(R.id.search_edit_text);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatentActivity.this,PublishPatentActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {
            String text = data.getStringExtra("content");
            content.setText(text);
            initDatas(UPDATE,classes);
        }
    }

    public void backClick(View v) {
        finish();
    }

    public void searchClick(View v) {
        startActivityForResult(new Intent(this,SearchActivity.class).putExtra("content","专利"),1);
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
        mPatents.clear();
        mAdapter.notifyDataSetChanged();
        mPageNumber = 1;
        initDatas(LOAD, classes);
    }
}
