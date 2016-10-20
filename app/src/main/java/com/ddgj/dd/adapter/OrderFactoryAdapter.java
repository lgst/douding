package com.ddgj.dd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class OrderFactoryAdapter extends BaseAdapter {
    private List<EnterpriseUser> mFactorys;

    public OrderFactoryAdapter(List<EnterpriseUser> mFactorys) {
        this.mFactorys = mFactorys;
    }

    @Override
    public int getCount() {
        return mFactorys.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_factory_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EnterpriseUser user = mFactorys.get(position);
        Glide.with(parent.getContext())
                .load(user.getHead_picture())
                .into(viewHolder.icon);
        viewHolder.name.setText(user.getFacilitator_name());
        viewHolder.field.setText(user.getFacilitator_field());
        loadImages(viewHolder, user, parent);
        return convertView;
    }

    private void loadImages(ViewHolder viewHolder, EnterpriseUser user, ViewGroup parent) {
        String str = user.getFacilitator_picture();
        String[] strs = str.split(",");
        ImageView[] cvs = new ImageView[]{viewHolder.img1, viewHolder.img2, viewHolder.img3};
        int index = 0;
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals("null")) {
                continue;
            }
            cvs[index].setVisibility(View.VISIBLE);
            Glide.with(parent.getContext())
                    .load(NetWorkInterface.HOST + "/" + strs[i])
                    .into(cvs[index]);
            index++;
            if (index >= 3) {
                break;
            }
//            switch (i) {
//                case 0:
//                    break;
//                case 1:
//                    viewHolder.img2.setVisibility(View.VISIBLE);
//                    Glide.with(parent.getContext())
//                            .load(NetWorkInterface.HOST + "/" + strs[i])
//                            .into(viewHolder.img2);
//                    break;
//                case 2:
//                    viewHolder.img3.setVisibility(View.VISIBLE);
//                    Glide.with(parent.getContext())
//                            .load(NetWorkInterface.HOST + "/" + strs[i])
//                            .into(viewHolder.img3);
//                    break;
//            }
        }
    }

    static class ViewHolder {
        public CircleImageView icon;
        public TextView name;
        public TextView field;
        public ImageView img1;
        public ImageView img2;
        public ImageView img3;

        public ViewHolder(View rootView) {
            this.icon = (CircleImageView) rootView.findViewById(R.id.icon);
            this.name = (TextView) rootView.findViewById(R.id.name);
            this.field = (TextView) rootView.findViewById(R.id.field);
            this.img1 = (ImageView) rootView.findViewById(R.id.img1);
            this.img2 = (ImageView) rootView.findViewById(R.id.img2);
            this.img3 = (ImageView) rootView.findViewById(R.id.img3);
        }

    }
}
