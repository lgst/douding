package com.ddgj.dd.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.JoinTenderActivity;
import com.ddgj.dd.bean.TenderInfo;
import com.ddgj.dd.bean.TenderOrder;
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

public class MTenderOrderFragment extends BaseFragment implements NetWorkInterface {

    private RecyclerView mListView;
    private List<TenderOrder> mTenderOs = new ArrayList<>();
    private int mPage = 1;
    private TenderOrderAdatpter mAdapter;

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
        mAdapter = new TenderOrderAdatpter();
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        OkHttpUtils.get().url(GET_MINE_TENDER_ORDER + "?invite_u_id=" + UserHelper.getInstance().getUser().getAccount_id()
                + "&pageNumber=" + mPage + "&pageSingle=1000")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("获取投标失败："+e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                logi("获取投标成功"+response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            TenderOrder to = new Gson().fromJson(str, TenderOrder.class);
                            mTenderOs.add(to);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    class TenderOrderAdatpter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.item_reward_order_item, parent,false);
            return new MTenderOrderFragment.TenderOrderAdatpter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder vh = (ViewHolder) holder;
            vh.mTitle.setText(mTenderOs.get(position).getInvite_t_title());
            String[] pic = mTenderOs.get(position).getInvite_t_picture().split(",");
            vh.mImage.setVisibility(View.GONE);
            for (String url : pic) {
                if (!pic.equals("null")) {
                    Glide.with(getContext()).load(HOST + "/" + url).error(R.drawable.ic_image_default).into(vh.mImage);
                    vh.mImage.setVisibility(View.VISIBLE);
                    break;
                }
            }
            vh.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TenderInfo mTender = new TenderInfo();
                    mTender.setInvite_t_id(mTenderOs.get(position).getInvite_t_id());
                    startActivity(new Intent(getActivity(), JoinTenderActivity.class)
                            .putExtra("data", mTender)
                    .putExtra("id",mTenderOs.get(position).getTender_id()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTenderOs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View rootView;
            ImageView mImage;
            TextView mTitle;

            public ViewHolder(View rootView) {
                super(rootView);
                this.rootView = rootView;
                this.mImage = (ImageView) rootView.findViewById(R.id.image);
                this.mTitle = (TextView) rootView.findViewById(R.id.title);
            }
        }
    }
}
