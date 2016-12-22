package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.RewardTenderAdapter;
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.RecycleViewDivider;
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

public class RewardListActivity extends BaseActivity implements NetWorkInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private TabLayout mTabBar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private FloatingActionButton mFab;
    private List<RewardInfo> mRewards = new ArrayList<RewardInfo>();
    private int mPage = 1;
    private int mPageSize = 10;
    private AppCompatTextView mSearch;
    private String keyWords = "";
    private String price = "";
    private String type = "";
    private boolean isClear;
    private boolean isRefreshing;
    private RewardTenderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_list);
        initView();
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("pageNumber", String.valueOf(mPage));
        params.put("pageSingle", String.valueOf(mPageSize));
        params.put("reward_state", "0");
        params.put("reward_price", price);
        params.put("reward_title", keyWords);
        params.put("reward_type", type);
        OkHttpUtils.post().url(GET_REWARD).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("获取悬赏失败：" + e.getMessage());
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                logi(response);
                if (isClear) {
                    mRewards.clear();
                    isClear = false;
                }
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            RewardInfo reward = new Gson().fromJson(str, RewardInfo.class);
                            mRewards.add(reward);
                        }
                    } else mPage--;
                    isRefreshing = false;
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
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
        mTabBar = (TabLayout) findViewById(R.id.tab_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RewardTenderAdapter<RewardInfo>(mRewards);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 && mFab.getVisibility() == View.VISIBLE) {
                    hideFab();
                } else if (dy < -10 && mFab.getVisibility() == View.INVISIBLE) {
                    showFab();
                }
                int last = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last >= mRewards.size() - 1 && !isRefreshing) {
                    isRefreshing = true;
                    mPage++;
                    initData();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mRecyclerView.addItemDecoration(
                new RecycleViewDivider(this, LinearLayout.VERTICAL, DensityUtil.dip2px(this, 8), getResources().getColor(R.color.grey_light)));
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
        mRefreshLayout.setOnRefreshListener(this);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        initTab();
        mFab.setOnClickListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        mSearch = (AppCompatTextView) findViewById(R.id.search);
        mSearch.setOnClickListener(this);
    }

    private void hideFab() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mFab.clearAnimation();
        mFab.setAnimation(anim);
    }

    private void showFab() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mFab.clearAnimation();
        mFab.setAnimation(anim);
    }

    private void initTab() {
        mTabBar.addTab(mTabBar.newTab().setText("分类"));
        mTabBar.addTab(mTabBar.newTab().setText("价格"));
        mTabBar.addTab(mTabBar.newTab().setText("中标任务"));
        mTabBar.addTab(mTabBar.newTab().setText("结束任务"));
        mTabBar.getTabAt(0).setCustomView(getTab("分类", R.drawable.ic_down_gray));
        mTabBar.getTabAt(1).setCustomView(getTab("价格", R.drawable.ic_down_gray));
        mTabBar.getTabAt(0).getCustomView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClassesMenu(v);
                mTabBar.setScrollPosition(0, 0.0f, true);
            }
        });
        mTabBar.getTabAt(1).getCustomView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceMenu(v);
                mTabBar.setScrollPosition(1, 0.0f, true);
            }
        });
    }

    private void showClassesMenu(final View v) {
        PopupMenu pm = new PopupMenu(this, v);
        pm.getMenuInflater().inflate(R.menu.pop_menu_reward_classes, pm.getMenu());
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all:
                        ((TextView) ((LinearLayout) v).getChildAt(1)).setText(item.getTitle());

                        break;
                }
                return false;
            }
        });
        pm.show();
    }

    private void showPriceMenu(final View v) {
        PopupMenu pm = new PopupMenu(this, v);
        pm.getMenuInflater().inflate(R.menu.pop_menu_reward_price, pm.getMenu());
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all:
                        price = "";
                        break;
                    default:
                        price = item.getTitle().toString();
                        break;
                }
                isClear = true;
                mPage = 1;
                ((TextView) ((LinearLayout) v).getChildAt(1)).setText(item.getTitle());
                initData();
                return true;
            }
        });
        pm.show();
    }

    private View getTab(String text, int icon) {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_tab, null);
        ImageView imageView = (ImageView) layout.getChildAt(0);
        TextView textView = (TextView) layout.getChildAt(1);
        imageView.setImageResource(icon);
        textView.setText(text);
        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (UserHelper.getInstance().isLogined())
                    startActivity(new Intent(this, AddRewardActivity.class));
                else {
                    startActivity(new Intent(this, LoginActivity.class));
                    showToastShort("请先登录！");
                }
                break;
            case R.id.search:
                startActivityForResult(new Intent(this, SearchActivity.class).putExtra("title", "悬赏"), 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {
            String text = data.getStringExtra("content");
            mSearch.setText(text);
            keyWords = text;
            isClear = true;
            initData();
        }
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        isClear = true;
        initData();
    }
}
