package com.ddgj.dd.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.net.NetWorkInterface;

import java.util.List;


/**
 * Created by Administrator on 2016/10/14/0014.
 */

public class FactoryAdapter extends BaseAdapter {

    private List<EnterpriseUser> facilitators;
    private Activity act;

    public FactoryAdapter(Activity act, List<EnterpriseUser> facilitators) {
        this.act = act;
        this.facilitators = facilitators;

    }

    @Override
    public int getCount() {
        return facilitators.size();
    }

    @Override
    public Object getItem(int position) {
        return facilitators.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder vr = null;
        if (view == null) {
            view = act.getLayoutInflater().inflate(R.layout.item_factory_list_factory, null);
            vr = new ViewHolder();
            vr.title = (TextView) view.findViewById(R.id.tv_factory_title);
            vr.field = (TextView) view.findViewById(R.id.tv_factory_field);
            vr.scale = (TextView) view.findViewById(R.id.tv_factory_scale);
            vr.address = (TextView) view.findViewById(R.id.tv_factory_address);
            vr.factoryPic = (ImageView) view.findViewById(R.id.img_factory_pic);
            view.setTag(vr);
        } else {
            vr = (ViewHolder) view.getTag();

        }
        EnterpriseUser facilitator = facilitators.get(position);
        showImage(act,facilitator,vr);
        vr.title.setText(facilitator.getFacilitator_name());
        vr.field.setText(facilitator.getFacilitator_field());
        vr.scale.setText(facilitator.getFacilitator_scale());
        vr.address.setText(facilitator.getFacilitator_address());
        return view;
    }

    private void showImage(Activity act, EnterpriseUser facilitator, ViewHolder vr) {
        if (facilitator.getFacilitator_picture() != null) {
            String[] imgs = facilitator.getFacilitator_picture().split(",");

            Glide.with(act)
                    .load(NetWorkInterface.HOST + "/" + imgs[0])
                    .thumbnail(0.1f)
                    .into(vr.factoryPic);
        }
    }

    class ViewHolder {

        ImageView factoryPic = null;
        TextView title = null;
        TextView field = null;
        TextView scale = null;
        TextView address = null;
    }
}
