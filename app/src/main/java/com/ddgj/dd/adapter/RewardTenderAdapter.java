package com.ddgj.dd.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.RewardDetailActivity;
import com.ddgj.dd.activity.TenderDetailActivity;
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.bean.TenderInfo;
import com.ddgj.dd.util.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */

public class RewardTenderAdapter<T> extends RecyclerView.Adapter {
    List<T> beans;
    private static final int REWARD = 1;
    private static final int TENDER = 2;

    public RewardTenderAdapter(List<T> beans) {
        this.beans = beans;
    }

    @Override
    public int getItemViewType(int position) {
        if (beans.get(position) instanceof RewardInfo)
            return REWARD;
        return TENDER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        final Object ser = beans.get(position);
        if (ser instanceof RewardInfo) {
            RewardInfo rewardInfo = (RewardInfo) ser;
            vh.mTimer.setText(
                    StringUtils.getTime(
                            rewardInfo.getReward_create_time(),
                            rewardInfo.getReward_cycle()
                    )
            );
            vh.mEndTime.setText(StringUtils.getEndTime(rewardInfo.getReward_create_time(),
                    rewardInfo.getReward_cycle()));
            vh.mNumber.setText(rewardInfo.getReward_number());
            vh.mPrice.setText(rewardInfo.getReward_price());
            vh.mStartTime.setText(rewardInfo.getReward_create_time());
            vh.mStatus.setText(null);
            vh.mTitle.setText(rewardInfo.getReward_title());
            vh.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh.rootView.getContext().startActivity(
                            new Intent(vh.rootView.getContext(), RewardDetailActivity.class)
                                    .putExtra("data", (Serializable) ser));
                }
            });
        } else if (ser instanceof TenderInfo) {
            TenderInfo tenderInfo = (TenderInfo) ser;
            vh.mTimer.setText(StringUtils.getTime(
                    tenderInfo.getInvite_t_c_time(),
                    tenderInfo.getInvite_t_cycle()
                    )
            );
            vh.mEndTime.setText(StringUtils.getEndTime(tenderInfo.getInvite_t_c_time(),
                    tenderInfo.getInvite_t_cycle()));
            vh.mNumber.setText(tenderInfo.getInvite_t_number());
            vh.mPrice.setText(tenderInfo.getInvite_t_price());
            vh.mStartTime.setText(tenderInfo.getInvite_t_c_time());
            vh.mStatus.setText(null);
            vh.mEndTime.setText(StringUtils.getEndTime(tenderInfo.getInvite_t_c_time(),
                    tenderInfo.getInvite_t_cycle()));
            vh.mTitle.setText(tenderInfo.getInvite_t_title());
            vh.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh.rootView.getContext().startActivity(
                            new Intent(vh.rootView.getContext(), TenderDetailActivity.class)
                                    .putExtra("data", (Serializable) ser));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        TextView mTitle;
        TextView mNumber;
        TextView mStartTime;
        TextView mEndTime;
        TextView mTimer;
        TextView mPrice;
        TextView mStatus;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.mTitle = (TextView) rootView.findViewById(R.id.title);
            this.mNumber = (TextView) rootView.findViewById(R.id.number);
            this.mStartTime = (TextView) rootView.findViewById(R.id.start_time);
            this.mEndTime = (TextView) rootView.findViewById(R.id.end_time);
            this.mTimer = (TextView) rootView.findViewById(R.id.timer);
            this.mPrice = (TextView) rootView.findViewById(R.id.price);
            this.mStatus = (TextView) rootView.findViewById(R.id.status);
        }

    }
}
