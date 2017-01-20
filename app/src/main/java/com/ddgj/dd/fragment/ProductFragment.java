package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.LoginActivity;
import com.ddgj.dd.activity.OriginalityDetailActivity;
import com.ddgj.dd.activity.PublishProductActivity;
import com.ddgj.dd.adapter.FactoryProductsAdapter;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.util.T;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetUtils;
import com.ddgj.dd.util.user.UserHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_ALL_ORIGINALITY;

/**
 * Created by Administrator on 2016/11/8.
 */

public class ProductFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private PullToRefreshListView mListView;
    private FloatingActionButton mFab;
    private String mKeyWords="";
    List<Originality> mProducts = new ArrayList<Originality>();
    private FactoryProductsAdapter mAdapter = new FactoryProductsAdapter(mProducts);
    private FragmentActivity act;
    /**
     * 页码
     */
    private int mPageNumber = 1;

    /**
     * 数量
     */
    private int mPageSingle = 10;
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;

    /**
     * 更新数据
     */
    private static final int RELOAD = 2;
    private HttpHelper<Originality> mHttpHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = (FragmentActivity) getActivity();
        mHttpHelper = new HttpHelper<Originality>(act, Originality.class);
        return inflater.inflate(R.layout.fragment_product, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData(LOAD);
    }

    public ProductFragment setmKeyWords(String keyWords) {
        this.mKeyWords = keyWords;
        return this;
    }

    public void search() {
        mListView.setRefreshing(true);
    }

    private void initData(final int flag) {
        if (!NetUtils.isConnected(getContext())) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("originality_name", mKeyWords);
        params.put("originality_differentiate", "1");
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
        mHttpHelper.getDatasPost(GET_ALL_ORIGINALITY, params, new DataCallback<Originality>() {
            @Override
            public void Failed(Exception e) {
                Log.e("lgst", "加载中国智造产品失败：" + e.getMessage());
                mListView.onRefreshComplete();
            }

            @Override
            public void Success(List<Originality> datas) {
                if (LOAD == flag)
                    mProducts.clear();
                mProducts.addAll(datas);
                mAdapter.notifyDataSetChanged();
                mListView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void initView() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (UserHelper.getInstance().getUser() != null && UserHelper.getInstance().getUser().getAccount_type().equals("1")) {
            fab.setVisibility(View.VISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getInstance().isLogined())
                    startActivity(new Intent(act, PublishProductActivity.class));
                else {
                    T.showShort(getContext(), "请先登录！");
                    startActivity(new Intent(act, LoginActivity.class).putExtra("flag", LoginActivity.BACK));
                }
            }
        });
        mListView = (PullToRefreshListView) findViewById(R.id.list_view);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber = 1;
                initData(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                initData(RELOAD);
            }
        });
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (UserHelper.getInstance().getUser() != null & UserHelper.getInstance().getUser() instanceof EnterpriseUser)
            mFab.setVisibility(View.VISIBLE);
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivity(new Intent(getActivity(), PublishProductActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Map<String, String> params = new HashMap<String, String>();
        Originality ori = mProducts.get(position - 1);
        ori.setOriginality_differentiate("1");
        Intent intent = new Intent(getActivity(), OriginalityDetailActivity.class);
        intent.putExtra("originality_id", ori.getOriginality_id());
        startActivity(intent);
//        params.put("client_side", "app");
//        params.put("originality_id", ori.getOriginality_id());
//        mHttpHelper.startDetailsPage(GET__PRODUCT_DETAILS, params, ori);
    }
}
