package com.ddgj.dd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.Facilitator;

import java.util.List;


/**
 * Created by Administrator on 2016/10/14/0014.
 */

public class FactoryAdapter extends BaseAdapter {

    private Context context;
    private List<Facilitator> itemList;
    private LayoutInflater layoutInflater;

    public FactoryAdapter(Context context, List<Facilitator> itemList, LayoutInflater layoutInflater){
        super();
        this.context = context;
        this.itemList = itemList;
        this.layoutInflater = layoutInflater;

    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null){
            view = layoutInflater.inflate(R
                    .layout.item_factory_list_factory,null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.tv_factory_title);
            viewHolder.field = (TextView) view.findViewById(R.id.tv_factory_field);
            viewHolder.scale = (TextView) view.findViewById(R.id.tv_factory_scale);
            viewHolder.address = (TextView) view.findViewById(R.id.tv_factory_address);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

        }
        viewHolder.title.setText(itemList.get(position).getFacilitator_name());
        viewHolder.field.setText(itemList.get(position).getFacilitator_field());
        viewHolder.scale.setText(itemList.get(position).getFacilitator_scale());
        viewHolder.address.setText(itemList.get(position).getFacilitator_address());
        return view;
    }

    class ViewHolder {

        TextView title = null;
        TextView field  = null;
        TextView scale  = null;
        TextView address  = null;
    }
}
