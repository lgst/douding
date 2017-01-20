package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.CitySelecterActivity;
import com.ddgj.dd.activity.FactoryDetailActivity;
import com.ddgj.dd.adapter.FactoryAdapter;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_FACILITATORMADE;

/**
 * Created by Administrator on 2016/11/8.
 */

public class FactoryFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private PullToRefreshListView mListView;
    private FragmentActivity act;
    private String mKeyWords = "";
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
    private Button mBtnCity;
    private Button mBtnType;
    private Button mBtnFiled;
    private ListView mFacTypeLv;
    private ListView mFacFiledLv;
    private String type = "0";
    private int filed;
    private FrameLayout mContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = (FragmentActivity) getActivity();
        mHttpHelper = new HttpHelper<EnterpriseUser>(act, EnterpriseUser.class);
        return inflater.inflate(R.layout.fragment_factory, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData(LOAD);
    }

    public FactoryFragment setmKeyWords(String mKeyWords) {
        this.mKeyWords = mKeyWords;
        return this;
    }

    public void search() {
        mListView.setRefreshing(true);
    }

    private void initData(final int flag) {
        if (!NetUtils.isConnected(getContext())) {
            return;
        }
        String address = mBtnCity.getText().toString().replace("全国", "");
//        String type = String.valueOf(mBtnType.getSelectedItemPosition());
//        int filed = mBtnFiled.getSelectedItemPosition();
        Map<String, String> params = new HashMap<String, String>();
        params.put("modify_differentiate", type);
        params.put("facilitator_type", "");
        params.put("facilitator_field", String.valueOf(filed == 0 ? "" : filed));
        params.put("facilitator_area", "");
        params.put("facilitator_name", mKeyWords);
        params.put("facilitator_address", address);
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle", String.valueOf(mPageSingle));
//        L.i("type:"+type+"   filed:"+params.get("facilitator_filed"));
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
                mAdapter.notifyDataSetChanged();
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
                mPageNumber = 1;
                initData(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                initData(RELOAD);
            }
        });
        mBtnCity = (Button) findViewById(R.id.btn_city);
        mBtnCity.setOnClickListener(this);
        mBtnType = (Button) findViewById(R.id.btn_type);
        mBtnType.setOnClickListener(this);
        mBtnFiled = (Button) findViewById(R.id.btn_filed);
        mBtnFiled.setOnClickListener(this);
        mFacTypeLv = (ListView) findViewById(R.id.fac_type_lv);
        mFacTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = String.valueOf(position);
                mBtnType.setText(((TextView) view).getText().toString());
                closeList((ListView) parent);
                search();
            }
        });
        mFacFiledLv = (ListView) findViewById(R.id.fac_filed_lv);
        mFacFiledLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filed = position;
                mBtnFiled.setText(((TextView) view).getText());
                closeList((ListView) parent);
                search();
            }
        });
        mContent = (FrameLayout) findViewById(R.id.content);
        mContent.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EnterpriseUser user = mFactorys.get(position - 1);
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("client_side", "app");
//        params.put("acilitator_id", user.getAccount_id());
        Intent intent = new Intent(getActivity(), FactoryDetailActivity.class);
        intent.putExtra("acilitator_id", user.getAcilitator_id());
        startActivity(intent);
//        mHttpHelper.startDetailsPage(GET_FAC_DETAILS,params,user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_city:
                startActivityForResult(new Intent(getActivity(), CitySelecterActivity.class), 10);
                break;
            case R.id.btn_type:
                mFacFiledLv.setVisibility(View.GONE);
                showList(mFacTypeLv);
                break;
            case R.id.btn_filed:
                mFacTypeLv.setVisibility(View.GONE);
                showList(mFacFiledLv);
                break;
            case R.id.content:
                closeList(mFacFiledLv);
                closeList(mFacTypeLv);
                break;
        }
    }

    private void showList(final ListView listView) {
        if (listView.getVisibility() == View.VISIBLE) {
            closeList(listView);
            return;
        }
        mContent.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_top);
        Animation anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_top);
        mContent.clearAnimation();
        mContent.setAnimation(anim1);
        listView.clearAnimation();
        listView.setAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void closeList(final ListView listView) {
        if(listView.getVisibility()==View.GONE)
            return;
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_top);
        Animation anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_top);
        mContent.clearAnimation();
        mContent.setAnimation(anim1);
        listView.clearAnimation();
        listView.setAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listView.setVisibility(View.GONE);
                mContent.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS && requestCode == 10) {
            mBtnCity.setText(data.getStringExtra("city"));
            search();
        }
    }
}
