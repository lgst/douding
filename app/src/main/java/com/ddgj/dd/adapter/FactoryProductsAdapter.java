package com.ddgj.dd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.util.net.NetWorkInterface;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14/0014.
 */

public class FactoryProductsAdapter extends BaseAdapter{

    private List<Originality> products;

    public FactoryProductsAdapter(List<Originality> facProOriginality) {
        this.products = facProOriginality;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder vr = null;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_factory_list_products, null);
            vr = new ViewHolder();
            vr.proTitle = (TextView) view.findViewById(R.id.tv_factory_products_title);
            vr.proContent = (TextView) view.findViewById(R.id.tv_factory_products_content);
            vr.proImg = (ImageView) view.findViewById(R.id.img_products_pic);
            view.setTag(vr);
        } else {
            vr = (ViewHolder) view.getTag();
        }
        Originality proOriginality = products.get(position);
        showImage(viewGroup.getContext(),proOriginality,vr);
        vr.proTitle.setText(proOriginality.getOriginality_name());
        vr.proContent.setText(proOriginality.getOriginality_details());
        return view;
    }

    private void showImage(Context context, Originality proOriginality, ViewHolder vr){
        if (proOriginality.getO_picture() != null){
            String[] imgs = proOriginality.getO_picture().split(",");
            Glide.with(context)
                    .load(NetWorkInterface.HOST + "/" + imgs[0])
                    .thumbnail(0.1f)
                    .error(R.drawable.ic_image_default)
                    .into(vr.proImg);
        }

    }

    class ViewHolder{
        ImageView proImg = null;
        TextView proTitle = null;
        TextView proContent = null;
    }
}
