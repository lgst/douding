package com.ddgj.dd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;

import java.util.List;

/**
 * Created by Administrator on 2016/10/16.
 */

public class OrderAdapter extends BaseAdapter {
    private List<Order> mOrders;

    public OrderAdapter(List<Order> orders) {
        mOrders = orders;
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Order order = mOrders.get(position);
        if (order.getMade_picture() != null) {
            String imgs[] = order.getMade_picture().split(",");
            for (int i = 0; i < imgs.length; i++) {
                if (imgs[i].isEmpty() || imgs[i].equals("null"))
                    continue;
                Glide.with(parent.getContext())
                        .load(NetWorkInterface.HOST + "/" + imgs[i])
                        .error(R.mipmap.ic_crop_original_grey600_48dp)
                        .placeholder(R.mipmap.ic_crop_original_grey600_48dp)
                        .thumbnail(0.1f)
                        .into(vh.img);
                break;
            }
        }
        vh.title_text.setText(order.getMade_title());
        vh.content_text.setText(order.getMade_describe());
        vh.browse.setText(String.valueOf((int) (Math.random() * 100)));
        vh.date.setText(StringUtils.getDate(order.getMade_time()));
        return convertView;
    }


    class ViewHolder {
        public View rootView;
        public ImageView img;
        public TextView title_text;
        public TextView content_text;
        public TextView browse;
        public TextView date;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.img = (ImageView) rootView.findViewById(R.id.img);
            this.title_text = (TextView) rootView.findViewById(R.id.title_text);
            this.content_text = (TextView) rootView.findViewById(R.id.content_text);
            this.browse = (TextView) rootView.findViewById(R.id.browse);
            this.date = (TextView) rootView.findViewById(R.id.date);
        }

    }
}
