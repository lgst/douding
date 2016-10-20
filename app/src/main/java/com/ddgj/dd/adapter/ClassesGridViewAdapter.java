package com.ddgj.dd.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.FactoryActivity;
import com.ddgj.dd.activity.OriginalityActivity;
import com.ddgj.dd.activity.PatentActivity;
//import com.ddgj.activity.ListActivity;

/**
 * Created by Administrator on 2016/10/6.
 */
public class ClassesGridViewAdapter extends BaseAdapter {
    private String[] names;
    private int[] imgs;
    private Context context;

    public ClassesGridViewAdapter(Context contexts, int[] imgs, String[] names) {
        this.context = contexts;
        this.imgs = imgs;
        this.names = names;
    }

    @Override
    public int getCount() {
        return 6;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_list_classes_item,null,false);
        ((ImageView)convertView.findViewById(R.id.img)).setImageResource(imgs[position]);
        ((TextView)convertView.findViewById(R.id.name)).setText(names[position]);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0://创意
                        context.startActivity(new Intent(context, OriginalityActivity.class));
                        break;
                    case 1://专利
                        context.startActivity(new Intent(context, PatentActivity.class));
                        break;
                    case 2://订制
//                        context.startActivity(new Intent(context, ListActivity.class).putExtra("page",3));
                        break;
                    case 3://代工
//                        context.startActivity(new Intent(context, ListActivity.class).putExtra("page",4));
                        break;
                    case 4://众筹
//                        context.startActivity(new Intent(context, ListActivity.class).putExtra("page",5));
                        break;
                    case 5://工厂 中国智造
                        context.startActivity(new Intent(context, FactoryActivity.class).putExtra("page",6));
                        break;
                }
            }
        });
        return convertView;
    }
}
