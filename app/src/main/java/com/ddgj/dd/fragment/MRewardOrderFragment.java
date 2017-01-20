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
import com.ddgj.dd.activity.JoinRewardActivity;
import com.ddgj.dd.bean.RewardOrder;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.RecycleViewDivider;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
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

public class MRewardOrderFragment extends BaseFragment implements NetWorkInterface {
    private RecyclerView mListView;
    private List<RewardOrder> mRewardOs = new ArrayList<>();
    private int mPage = 0;
    private RewardOrderAdatpter mAdapter;
    private DbUtils mDbu;
    private boolean noMore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_m_tender_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDbu = DbUtils.create(getContext(), StringUtils.getDbName());
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mListView = (RecyclerView) findViewById(R.id.list_view);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dip2px(getContext(), 8), Color.parseColor("#EEEEEE")));
        mAdapter = new RewardOrderAdatpter();
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
//        if(noMore)
//            return;
//        try {
//            List<RewardOrder> ros = mDbu.findAll(Selector.from(RewardOrder.class).limit(10).offset(mPage));
//            if(ros!=null){
//                mRewardOs.addAll(ros);
//                mAdapter.notifyDataSetChanged();
//            }
//            if(ros==null||ros.size()<10){
//                noMore = true;
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
        OkHttpUtils.get().url(GET_MINE_REWARD_ORDER + "?reward_u_id=" +
                UserHelper.getInstance().getUser().getAccount_id()
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
                            RewardOrder ro = new Gson().fromJson(str, RewardOrder.class);
                            mRewardOs.add(ro);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class RewardOrderAdatpter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.item_reward_order_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder vh = (ViewHolder) holder;
            vh.mTitle.setText(mRewardOs.get(position).getReward_title());
            if (mRewardOs.get(position).getReward_picture() != null) {
                String[] pic = mRewardOs.get(position).getReward_picture().split(",");
                vh.mImage.setVisibility(View.GONE);
                for (String url : pic) {
                    if (!url.equals("null")) {
                        Glide.with(getContext()).load(HOST + "/" + url).error(R.drawable.ic_image_default).into(vh.mImage);
                        vh.mImage.setVisibility(View.VISIBLE);
                        break;
                    }

                }
            }
            vh.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), JoinRewardActivity.class)
                            .putExtra("data", mRewardOs.get(position)));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRewardOs.size();
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
