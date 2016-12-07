package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.OriginalityPLVAdapter;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.ListScrollDistanceCalculator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OriginalityActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, NetWorkInterface {

    private PullToRefreshListView mplv;
    private List<Originality> mOriginalitys;
    private OriginalityPLVAdapter mAdapter;
    private RadioGroup mRg;
    //    private LinearLayout mLoading;
    private TextView content;
    /**
     * 页码
     */
    private int mPageNumber = 1;
    /**
     * 数量
     */
    private int mPageSingle = 15;
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
    private HttpHelper<Originality> mOriHttpHelper;
    private View notDataView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_originality);
        mOriginalitys = new ArrayList<Originality>();
        mAdapter = new OriginalityPLVAdapter(this, mOriginalitys);
        mOriHttpHelper = new HttpHelper<Originality>(this, Originality.class);
        initView();
//        initDatas(LOAD, classes);
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
        params.put("originality_name", content.getText().toString().trim());
        if (classes == MINE) {
            params.put("o_account_id", UserHelper.getInstance().getUser().getAccount_id());
        } else {
            params.put("originality_differentiate", String.valueOf(0));
        }

        mOriHttpHelper.getDatasPost(getUrl(classes), params, new DataCallback<Originality>() {
            @Override
            public void Failed(Exception e) {
                mPageNumber--;
                Log.e(TAG, "Failed: " + e.getMessage());
                //关闭刷新
                mplv.onRefreshComplete();
            }

            @Override
            public void Success(List<Originality> datas) {
                if (flag == LOAD)
                    mOriginalitys.clear();
                mOriginalitys.addAll(datas);
                mAdapter.notifyDataSetChanged();
                if (mplv.isRefreshing())//关闭刷新
                    mplv.onRefreshComplete();
                if (!mOriginalitys.isEmpty())//关闭加载数据页面
                    notDataView.setVisibility(View.GONE);
                else
                    notDataView.setVisibility(View.VISIBLE);
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
//        mLoading = (LinearLayout) findViewById(R.id.loading);
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
                originality.setOriginality_differentiate("0");
              // mOriHttpHelper.startDetailsPage(GET_ORIGINALITY_DETAILS, params, originality);
                Intent intent = new Intent(OriginalityActivity.this, OriginalityDetailActivity.class);
                intent.putExtra("originality_id",originality.getOriginality_id());
                startActivity(intent);
            }
        });
        mplv.setAdapter(mAdapter);
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
        ListScrollDistanceCalculator lsdc = new ListScrollDistanceCalculator();
        lsdc.setScrollDistanceListener(new ListScrollDistanceCalculator.ScrollDistanceListener() {
            @Override
            public void onScrollDistanceChanged(int delta, int total) {
                Log.i(TAG, "delta:" + delta + "  total:" + total);
                if (total > 0) {
                    if (is) {
                        floatingActionButton.clearAnimation();
                        Animation anim = AnimationUtils.loadAnimation(OriginalityActivity.this, R.anim.slide_in_from_bottom);
                        floatingActionButton.setAnimation(anim);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                floatingActionButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        is = false;
                    }
                } else {
                    if (!is) {
                        floatingActionButton.clearAnimation();
                        Animation anim = AnimationUtils.loadAnimation(OriginalityActivity.this, R.anim.slide_out_to_bottom);
                        floatingActionButton.setAnimation(anim);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                floatingActionButton.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        is = true;
                    }
                }
            }
        });
        mplv.setOnScrollListener(lsdc);
        mplv.setRefreshing(true);
        notDataView = findViewById(R.id.not_data);
//        ((ViewGroup)mplv.getParent()).addView(notDataView);
//        mplv.setEmptyView(notDataView);
    }

    private boolean is = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {
            String text = data.getStringExtra("content");
            content.setText(text);
            initDatas(LOAD, classes);
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
//        if (mLoading.getVisibility() == View.VISIBLE) {
//            return;
//        }
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
//        mLoading.setVisibility(View.VISIBLE);
        mOriginalitys.clear();
        mAdapter.notifyDataSetChanged();
        mPageNumber = 1;
        mplv.setRefreshing(true);
//        initDatas(LOAD, classes);
    }
}