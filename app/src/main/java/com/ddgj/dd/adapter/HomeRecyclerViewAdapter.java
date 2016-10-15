package com.ddgj.dd.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.ADBean;
import com.ddgj.dd.fragment.dummy.HomeListContent;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CustomGridView;
import com.google.gson.Gson;
import com.hejunlin.superindicatorlibray.CircleIndicator;
import com.hejunlin.superindicatorlibray.LoopViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 */
public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NetWorkInterface {

    private final List<Object> mValues;
    private Activity act;

    public HomeRecyclerViewAdapter(Activity act, List<Object> items) {
        mValues = items;
        this.act = act;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
//        Log.i("lgst","viewType:"+viewType);
        switch (viewType) {
            case 0://轮播图
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_list_ad, parent, false);
//                Log.i("lgst", "lunbotu");
                return new ViewHolderAD(view);
            case 1://分类
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_list_class, parent, false);
//                Log.i("lgst", "class");
                return new ViewHolderClasses(view);
            case 2://
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_list_title, parent, false);
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderAD) {//轮播图
            OkHttpUtils.post().url(NetWorkInterface.GET_AD).id(100).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject jo = new JSONObject(response);
                        JSONArray array = jo.getJSONArray("data");
                        Log.i("lgst",array.toString());

                        List<ADBean> adBeens = new ArrayList<ADBean>();
                        for (int i = 0; i <array.length();i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            ADBean adBean = new Gson().fromJson(jsonObject.toString(),ADBean.class);
                            adBeens.add(adBean);
                            ImageView imageView = (ImageView) act.getLayoutInflater().inflate(R.layout.item_home_list_ad_item, null);
                            Glide.with(act)
                                    .load(HOST + "/" + adBean.getPicture())
                                    .thumbnail(0.1f)
                                    .into(imageView);
                        }
                        LoopViewPager viewpager = (LoopViewPager) ((ViewHolderAD) holder).vp;
                        CircleIndicator indicator = ((ViewHolderAD) holder).point;
                        viewpager.setAdapter(new ADAdapter(act,adBeens));
                        viewpager.setLooperPic(true);//是否设置自动轮播
                        indicator.setViewPager(viewpager);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (holder instanceof ViewHolderClasses) {//分类
            HomeListContent.Classes classes = (HomeListContent.Classes) mValues.get(position);
            ViewHolderClasses viewHolderClasses = ((ViewHolderClasses) holder);
            viewHolderClasses.customGridView.setAdapter(new ClassesGridViewAdapter(act, classes.getIMGS(), classes.getNames()));
        }
    }

    @Override
    public int getItemCount() {
//        Log.i("lgst","ItemCount:"+mValues.size());
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolderAD extends RecyclerView.ViewHolder {
        public final LoopViewPager vp;
        public final CircleIndicator point;

        public ViewHolderAD(View view) {
            super(view);
            vp = (LoopViewPager) view.findViewById(R.id.viewpager);
            point = (CircleIndicator) view.findViewById(R.id.indicator);
        }
    }

    class ViewHolderClasses extends RecyclerView.ViewHolder {
        public final CustomGridView customGridView;

        public ViewHolderClasses(View itemView) {
            super(itemView);
            customGridView = (CustomGridView) itemView;
        }
    }
}
