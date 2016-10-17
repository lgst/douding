package com.ddgj.dd.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.WebActivity;
import com.ddgj.dd.bean.ADBean;
import com.ddgj.dd.util.net.NetWorkInterface;

import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */
public class ADAdapter extends PagerAdapter implements NetWorkInterface {
    private Activity act;
    private List<ADBean> ads;

    public ADAdapter( Activity act,List<ADBean> ads) {
        this.ads = ads;
        this.act = act;
    }

    @Override
    public int getCount() {
        return ads.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) act.getLayoutInflater().inflate(R.layout.item_home_list_ad_item, null);
        final ADBean ad = ads.get(position);
        Glide.with(act)
                .load(HOST + "/" + ad.getPicture())
                .placeholder(R.mipmap.ic_image_white_48dp)
                .error(R.mipmap.ic_image_white_48dp)
                .thumbnail(0.1f)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lgst","Url:"+ad.getLink_address());
                act.startActivity(new Intent(act, WebActivity.class)
                        .putExtra("url","http://"+ad.getLink_address())
                .putExtra("title",ad.getNote()));
            }
        });
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        notifyDataSetChanged();
    }
}
