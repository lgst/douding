package com.ddgj.dd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.FavoriteInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */

public class FavoriteAdapter extends BaseAdapter {
    List<FavoriteInfo> favoriteInfos;
    /*0为专利 1为创意 2私人订制 3为代工产品 4为订制厂家 5 为代工厂家 6为中国智造企业 7创意产品 8为帖子*/
    private static final String[] TYPES = {"专利","创意","私人订制","代工产品","订制工厂","代工工厂","工厂","创意产品","帖子"};

    public FavoriteAdapter(List<FavoriteInfo> favoriteInfos) {
        this.favoriteInfos = favoriteInfos;
    }

    @Override
    public int getCount() {
        return favoriteInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return favoriteInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        FavoriteInfo favoriteInfo = favoriteInfos.get(position);
        vh.mTitle.setText(favoriteInfo.getC_u_account());
        vh.mContentText.setText(favoriteInfo.getC_from_title());
        Glide.with(parent.getContext())
                .load(NetWorkInterface.HOST+"/"+favoriteInfo.getHead_picture())
                .into(vh.mIcon);
        vh.mType.setText(TYPES[Integer.parseInt(favoriteInfo.getCollection_type())]);
        return convertView;
    }

    public static class ViewHolder {
        public View rootView;
        public CircleImageView mIcon;
        public TextView mTitle;
        public TextView mContentText;
        public TextView mType;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mIcon = (CircleImageView) rootView.findViewById(R.id.icon);
            this.mTitle = (TextView) rootView.findViewById(R.id.title);
            this.mContentText = (TextView) rootView.findViewById(R.id.content_text);
            this.mType = (TextView) rootView.findViewById(R.id.type);
        }

    }
}
