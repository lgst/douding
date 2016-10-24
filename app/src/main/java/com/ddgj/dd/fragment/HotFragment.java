package com.ddgj.dd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Administrator on 2016/10/20.
 */

public class HotFragment extends BaseFragment {
    public static String title[]=new String[]{"魏**","王**","李**","宽**","土**","丸**","张*","丸**","木**","赵**","香**"};
    public static String info[]=new String[]{ "味道不错，价格公道","分量足","味道不错，价格公道","味道不错，价格公道","好吃不贵，在此不来","口感不错，非常好吃","价钱便宜，实惠多多","味道好，啦啦啦","不好吃，难吃死了","黑点不好ichi","贵不好治" };
    public static String time[]=new String[]{ "2016-3-3","2016-3-1","2016-3-4","2016-3-4","2016-3-3","2016-3-5","2016-3-4","2016-3-3","2016-3-6","2016-3-6","2016-3-6" };
    private PullToRefreshListView pullToRefreshView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_hot, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    protected void initViews() {

        pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        pullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

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

    private class PlateAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public PlateAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return title.length;
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
                //可以理解为从vlist获取view  之后把view返回给ListView
                convertView = mInflater.inflate(R.layout.plate_all_list, null);
                holder.username = (TextView)convertView.findViewById(R.id.username);
                holder.info = (TextView)convertView.findViewById(R.id.info);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.username.setText(title[position]);
            holder.info.setText(info[position]);
            holder.time.setText(time[position]);

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
