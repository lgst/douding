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

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.PlateDetailsActivity;
import com.ddgj.dd.bean.PostBean;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CircleImageView;
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

public class HotFragment extends BaseFragment {
    /**
     * 重新加载数据
     */
    private static final int LOAD = 1;
    /**
     * 更新数据
     */
    private static final int UPDATE = 2;
    private int mPageNumber = 1;
    private List<PostBean> postBeanList = new ArrayList<PostBean>();
    private PullToRefreshListView pullToRefreshView;
    private PlateAdapter plateAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_hot, null);
        initdatas(LOAD);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initdatas(final int flag) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(mPageNumber));//第几页
        params.put("pageSingle", String.valueOf(10));//每页分的数据条数10条
        params.put("bbs_type", "1");//这留个坑
        OkHttpUtils.post().url(NetWorkInterface.GET_HOT_POST).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pullToRefreshView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("shuju", response);
                JSONObject jo = null;
                try {
                    jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (status == STATUS_SUCCESS) {
                        JSONArray ja = jo.getJSONArray("data");
                        if (LOAD == flag) {
                            postBeanList.clear();
                        }
                        for (int i = 0; i < ja.length(); i++) {
                            String string = ja.getJSONObject(i).toString();
                            PostBean postBean = new Gson().fromJson(string, PostBean.class);
                            postBeanList.add(postBean);
                        }
                        plateAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    pullToRefreshView.onRefreshComplete();
                }
            }
        });
    }

    @Override
    protected void initView() {
        pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        pullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                postBeanList.clear();
                plateAdapter.notifyDataSetChanged();
                mPageNumber = 1;
                initdatas(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                initdatas(UPDATE);
            }
        });

        plateAdapter = new PlateAdapter(getActivity());
        pullToRefreshView.setAdapter(plateAdapter);
        pullToRefreshView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PlateDetailsActivity.class).putExtra("post_id", postBeanList.get(i - 1).getId());
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
                holder = new ViewHolder();
                //可以理解为从vlist获取view  之后把view返回给ListView
                convertView = mInflater.inflate(R.layout.plate_all_list, null);
                holder.username = (TextView) convertView.findViewById(R.id.username);
                holder.info = (TextView) convertView.findViewById(R.id.info);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.browseNumber = (TextView) convertView.findViewById(R.id.browse_number);
                holder.commentNumber = (TextView) convertView.findViewById(R.id.comment_number);
                holder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.username.setText(postBeanList.get(position).getAccount());
            holder.info.setText(postBeanList.get(position).getTitle());
            holder.time.setText(StringUtils.getDate(postBeanList.get(position).getSend_date()));
            holder.browseNumber.setText(String.valueOf(postBeanList.get(position).getViews()));
            holder.commentNumber.setText(String.valueOf(postBeanList.get(position).getComment_amount()));
            Glide.with(getContext())
                    .load(NetWorkInterface.HOST+"/"+postBeanList.get(position).getHead_picture())
                    .into(holder.icon);
            return convertView;
        }
    }

    //提取出来方便点
    public final class ViewHolder {
        public TextView username;
        public TextView info;
        public TextView time;
        public TextView browseNumber;
        public TextView commentNumber;
        public CircleImageView icon;
    }
}
