package com.ddgj.dd.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.Purchase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23/0023.
 * 采购Adapter
 */

public class PurchaseAdapter extends BaseAdapter {

    private List<Purchase> purchaseers = new ArrayList<Purchase>();
    private Activity act;


    public PurchaseAdapter(Activity act, List<Purchase> purchaseers) {
        this.act = act;
        this.purchaseers = purchaseers;
    }


    @Override
    public int getCount() {
        return this.purchaseers.size();
    }

    @Override
    public Object getItem(int i) {
        return purchaseers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
   /* public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Comment comment = comments.get(position);
        Glide.with(parent.getContext())
                .load(NetWorkInterface.HOST + "/" + comment.getHead_picture())
                .into(vh.mIcon);
        vh.mContent.setText(comment.getC_content());
        vh.mNickName.setText(comment.getNickname());
        vh.mTiem.setText(StringUtils.getDate(comment.getComment_time()));
        if (UserHelper.getInstance().getUser().getAccount().equals(comment.getAccount())) {
            vh.mSendMessage.setVisibility(View.INVISIBLE);
        } else {
            vh.mSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.getContext().startActivity(
                            new Intent(parent.getContext(), ChatActivity.class)
                                    .putExtra(EaseConstant.EXTRA_USER_ID, comment.getAccount()));
                }
            });
        }
        return convertView;
    }*/

    @Override
    public View getView(int i, View convertView, final ViewGroup purchase) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = act.getLayoutInflater().inflate(R.layout.item_home_list_purchase, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Purchase ph = purchaseers.get(i);

        vh.mTitle.setText(ph.getProcurement_name());
        vh.mContentText.setText(ph.getProcurement_describe());
        vh.mType.setText(ph.getProcurement_classes());
        vh.mDate.setText(ph.getProcurement_time());
        return convertView;
    }

    public static class ViewHolder {
        public View rootView;
        public TextView mTitle;
        public TextView mContentText;
        public TextView mType;
        public TextView mDate;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mTitle = (TextView) rootView.findViewById(R.id.title);
            this.mContentText = (TextView) rootView.findViewById(R.id.content_text);
            this.mType = (TextView) rootView.findViewById(R.id.type);
            this.mDate = (TextView) rootView.findViewById(R.id.date);
        }

    }
}
