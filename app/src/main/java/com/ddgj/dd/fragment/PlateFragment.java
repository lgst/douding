package com.ddgj.dd.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.PublishBBSActivity;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.PostBean;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

/**
 * Created by Administrator on 2016/10/20.
 */

public class PlateFragment extends BaseFragment {

    private List<PostBean> postBeanList=new ArrayList<PostBean>();
    public static String title[]=new String[]{"魏**","王**","李**","宽**","土**","丸**","张*","丸**","木**","赵**","香**"};
    public static String info[]=new String[]{ "味道不错，价格公道","分量足","味道不错，价格公道","味道不错，价格公道","好吃不贵，在此不来","口感不错，非常好吃","价钱便宜，实惠多多","味道好，啦啦啦","不好吃，难吃死了","黑点不好ichi","贵不好治" };
    public static String time[]=new String[]{ "2016-3-3","2016-3-1","2016-3-4","2016-3-4","2016-3-3","2016-3-5","2016-3-4","2016-3-3","2016-3-6","2016-3-6","2016-3-6" };
    private PullToRefreshListView pullToRefreshView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_plate, null);
        initdatas();
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

    }

    private void initdatas() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(1));//第几页
        params.put("pageSingle", String.valueOf(10));//每页分的数据条数10条
        params.put("bbs_type", "1");//坑
        OkHttpUtils.post().url(NetWorkInterface.GET_ALL_POST).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pullToRefreshView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("shuju",response);
                JSONObject jo = null;
                try {
                    jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (status==STATUS_SUCCESS){
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String string = ja.getJSONObject(i).toString();
                            PostBean postBean = new Gson().fromJson(string, PostBean.class);
                            postBeanList.add(postBean);
                            Log.e("postbean",postBeanList.get(i).getAccount());
                            Log.e("postbean",postBeanList.get(i).getTitle());
                            Log.e("postbean",postBeanList.get(i).getSend_date());
                            Log.e("postbean", String.valueOf(postBeanList.get(i).getViews()));
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pullToRefreshView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void initViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PublishBBSActivity.class);
                startActivity(intent);
            }
        });
        pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        pullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                postBeanList.clear();
                initdatas();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initdatas();
                pullToRefreshView.onRefreshComplete();
            }
        });

        PlateAdapter plateAdapter = new PlateAdapter(getActivity());
        pullToRefreshView.setAdapter(plateAdapter);
        pullToRefreshView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PlateDetailsActivity.class);
                startActivity(intent);
            }
        });

    }




    private class PlateAdapter extends BaseAdapter{
        private LayoutInflater mInflater;

        public PlateAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return postBeanList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.plate_all_list, null);
                holder.username = (TextView)convertView.findViewById(R.id.username);
                holder.info = (TextView)convertView.findViewById(R.id.info);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.username.setText(postBeanList.get(position).getAccount());
            holder.info.setText(postBeanList.get(position).getTitle());
            holder.time.setText(postBeanList.get(position).getSend_date());

            return convertView;
        }
    }
    //提取出来方便点
    public final class ViewHolder {
        public TextView username;
        public TextView info;
        public TextView time;

    }
}
