package com.ddgj.dd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddgj.dd.R;

/**
 * Created by Administrator on 2016/10/16.
 */

public class OrderClassesAdapter extends BaseAdapter {
    private int[] imgs;
    private String[] names;
    private Context context;

    public OrderClassesAdapter(Context context) {
        this.context = context;
        imgs = new int[]{
                R.mipmap.ic_furniture,
                R.mipmap.ic_clothing,
                R.mipmap.ic_gift,
                R.mipmap.ic_machine,
                R.mipmap.ic_electric,
                R.mipmap.ic_other,
                R.mipmap.ic_order_factory,
                R.mipmap.ic_add};
        names = context.getResources().getStringArray(R.array.order_classes_name);
    }

    @Override
    public int getCount() {
        return names.length;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_order_classes_item,null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        TextView textView = (TextView) convertView.findViewById(R.id.name);
        imageView.setImageResource(imgs[position]);
        textView.setText(names[position]);
        return convertView;
    }
}
