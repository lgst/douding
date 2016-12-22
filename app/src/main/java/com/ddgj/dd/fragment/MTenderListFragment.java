package com.ddgj.dd.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.RewardTenderAdapter;
import com.ddgj.dd.bean.TenderInfo;
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
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/6.
 */

public class MTenderListFragment extends BaseFragment implements NetWorkInterface {

    private RecyclerView mListView;
    private List<TenderInfo> mTenders = new ArrayList<>();
    private int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_m_tender_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    @Override
    protected void initView() {
        mListView = (RecyclerView) findViewById(R.id.list_view);
        mListView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dip2px(getContext(), 8), Color.parseColor("#EEEEEE")));
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(new RewardTenderAdapter(mTenders));
    }

    private void initData() {
        OkHttpUtils.get().url(GET_MINE_TENDER + "?invite_t_u_id=" + UserHelper.getInstance().getUser().getAccount_id()
                + "&pageNumber=" + mPage + "&pageSingle=10")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge(e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                logi(response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            TenderInfo tender = new Gson().fromJson(str, TenderInfo.class);
                            mTenders.add(tender);
                        }
                        mListView.getAdapter().notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
