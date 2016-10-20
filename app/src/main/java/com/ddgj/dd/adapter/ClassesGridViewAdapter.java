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
import com.ddgj.dd.activity.OEMActivity;
import com.ddgj.dd.activity.OrderActivity;
import com.ddgj.dd.activity.OriginalityActivity;
import com.ddgj.dd.activity.PatentActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;
//import com.ddgj.activity.ListActivity;

/**
 * Created by Administrator on 2016/10/6.
 */
public class ClassesGridViewAdapter extends BaseAdapter {
    private String[] names = new String[]{"个人创意", "专利服务", "私人订制", "委托代工", "全民众筹", "中国智造"};
    private final int[] IMGS = new int[]{
            R.mipmap.ic_home_classes_chuangyi,
            R.mipmap.ic_home_classes_zhuangli,
            R.mipmap.ic_home_classes_dingzhi,
            R.mipmap.ic_home_classes_daigong,
            R.mipmap.ic_home_classes_zhongchou,
            R.mipmap.ic_home_classes_gongchang};
    private Context context;

    public ClassesGridViewAdapter(Context contexts) {
        this.context = contexts;
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
        ((ImageView)convertView.findViewById(R.id.img)).setImageResource(IMGS[position]);
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
                        context.startActivity(new Intent(context, OrderActivity.class).putExtra("page",3));
                        break;
                    case 3://代工
                        context.startActivity(new Intent(context, OEMActivity.class).putExtra("page",4));
                        break;
                    case 4://众筹
//                        context.startActivity(new Intent(context, .class).putExtra("page",5));
                        break;
                    case 5://工厂 中国智造
                        context.startActivity(new Intent(context, FactoryActivity.class).putExtra("page",6));
                        break;
                }
            }
        });
        return convertView;
    }

    public SweetAlertDialog showLoadingDialog(String title, String content){
        SweetAlertDialog dialog = new SweetAlertDialog(context,SweetAlertDialog.NORMAL_TYPE);
        dialog.setContentText(content)
                .setTitleText(title)
                .show();
//        dialog.setCancelable(false);
        dialog.setConfirmText("关闭");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        return dialog;
    }
}
