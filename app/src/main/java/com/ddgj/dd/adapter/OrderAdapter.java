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
    private final int[] colors;
    //    0为等待接单 1为已接单 2为成功 3为失败 4服务方申请合作 5服务方申请验收
    private String[] STATUS = {"等待接单", "工作中", "交易成功", "交易失败", "待确认合作", "待验收"};
    private List<Order> mOrders;

    public OrderAdapter(List<Order> orders) {
        mOrders = orders;
        colors = new int[]{R.color.waiting,
                R.color.working,
                R.color.finished,
                R.color.grey,
                R.color.colorPrimary,
                R.color.blue};
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
        vh.img.setVisibility(View.GONE);
        if (order.getMade_picture() != null) {
            String imgs[] = order.getMade_picture().split(",");
            for (int i = 0; i < imgs.length; i++) {
                if (imgs[i].isEmpty() || imgs[i].equals("null"))
                    continue;
                vh.img.setVisibility(View.VISIBLE);
                Glide.with(parent.getContext())
                        .load(NetWorkInterface.HOST + "/" + imgs[i])
                        .error(R.drawable.ic_image_default)
                        .thumbnail(0.1f)
                        .into(vh.img);
                break;
            }
        }
        vh.title_text.setText(order.getMade_title());
        vh.content_text.setText(order.getMade_describe());
        vh.mAddress.setText(order.getMade_u_address());
        vh.browse.setText(order.getMade_price());
        vh.date.setText(StringUtils.getDate(order.getMade_time()));
        int status = Integer.parseInt(order.getMade_state());
        vh.status.setText(STATUS[status]);
        vh.status.setBackgroundColor(parent.getResources().getColor(colors[status]));
        return convertView;
    }


    public static class ViewHolder {
        View rootView;
        ImageView img;
        TextView title_text;
        TextView content_text;
        TextView browse;
        TextView date;
        TextView status;
        TextView mAddress;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mAddress = (TextView) rootView.findViewById(R.id.address);
            this.img = (ImageView) rootView.findViewById(R.id.img);
            this.title_text = (TextView) rootView.findViewById(R.id.title_text);
            this.content_text = (TextView) rootView.findViewById(R.id.content_text);
            this.browse = (TextView) rootView.findViewById(R.id.browse);
            this.date = (TextView) rootView.findViewById(R.id.date);
            this.status = (TextView) rootView.findViewById(R.id.tv_status);
        }

    }

}
