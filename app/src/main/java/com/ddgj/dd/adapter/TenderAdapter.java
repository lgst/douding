package com.ddgj.dd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.TenderInfo;
import com.ddgj.dd.util.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class TenderAdapter extends BaseAdapter {
    private List<TenderInfo> mTenders;

    public TenderAdapter(List<TenderInfo> mRewards) {
        this.mTenders = mRewards;
    }

    @Override
    public int getCount() {
        return mTenders.size();
    }

    @Override
    public Object getItem(int position) {
        return mTenders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        TenderInfo tender = mTenders.get(position);
        vh.mEndTime.setText(StringUtils.getEndTime(tender.getInvite_t_c_time(),tender.getInvite_t_cycle()));
        vh.mPrice.setText("￥ " + tender.getInvite_t_price());
        vh.mStartTime.setText(tender.getInvite_t_c_time());
        vh.mTimer.setText(StringUtils.getTime(tender.getInvite_t_c_time(),tender.getInvite_t_cycle()));
        vh.mStatus.setText("投标中");
        vh.mTitle.setText(tender.getInvite_t_title());
        vh.mNumber.setText(tender.getInvite_t_number());
        return convertView;
    }


    public static class ViewHolder {
        View rootView;
        TextView mStartTime;
        TextView mEndTime;
        TextView mTimer;
        TextView mPrice;
        TextView mStatus;
        TextView mTitle;
        TextView mNumber;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mStartTime = (TextView) rootView.findViewById(R.id.start_time);
            this.mEndTime = (TextView) rootView.findViewById(R.id.end_time);
            this.mTimer = (TextView) rootView.findViewById(R.id.timer);
            this.mPrice = (TextView) rootView.findViewById(R.id.price);
            this.mStatus = (TextView) rootView.findViewById(R.id.status);
            this.mTitle = (TextView) rootView.findViewById(R.id.title);
            this.mNumber = (TextView) rootView.findViewById(R.id.number);
        }
    }
}
