package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.adapter.FactoryAdapter;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_FACILITATORMADE;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_FAC_DETAILS;

/**
 * Created by Administrator on 2016/11/8.
 */

public class FactoryFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private PullToRefreshListView mListView;
    private BaseActivity act;
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
    private HttpHelper<EnterpriseUser> mHttpHelper;
    private List<EnterpriseUser> mFactorys = new ArrayList<EnterpriseUser>();
    private FactoryAdapter mAdapter = new FactoryAdapter(mFactorys);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = (BaseActivity) getActivity();
        mHttpHelper = new HttpHelper<EnterpriseUser>(act, EnterpriseUser.class);
        return inflater.inflate(R.layout.fragment_factory, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData(LOAD);
    }

    private void initData(final int flag) {
        if (!act.checkNetWork()) {
            act.showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("modify_differentiate", String.valueOf("0"));
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
        mHttpHelper.getDatasPost(GET_FACILITATORMADE, params, new DataCallback<EnterpriseUser>() {
            @Override
            public void Failed(Exception e) {
                Log.e("lgst", "获取中国智造工厂失败：" + e.getMessage());
                mListView.onRefreshComplete();
            }

            @Override
            public void Success(List<EnterpriseUser> datas) {
                if (LOAD == flag)
                    mFactorys.clear();
                mFactorys.addAll(datas);
                mListView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber=1;
                initData(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                initData(RELOAD);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EnterpriseUser user = mFactorys.get(position-1);
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_side", "app");
        params.put("acilitator_id", user.getAccount_id());
        mHttpHelper.startDetailsPage(GET_FAC_DETAILS,params,user);
    }
}
