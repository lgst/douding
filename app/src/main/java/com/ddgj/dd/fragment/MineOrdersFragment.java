package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.OrdersDetilActivity;
import com.ddgj.dd.bean.Orders;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/22.
 */

public class MineOrdersFragment extends BaseFragment implements NetWorkInterface {

    private ListView mLv;
    private String mClasses;
    private int mPageNumber = 1;
    private int mPageSingle = 10;
    private List<Orders> mOrdersList = new ArrayList<Orders>();
    private LVAdapter mAdapter;
    // 1交易中 2交易成功 3交易失败 4取消订单 5申请验收 6拒绝验收 7确认合作 8拒绝合作
    private String[] STATUS = {"", "待确认合作", "交易成功", "交易失败", "订单被取消", "验收中", "验收失败", "工作中", "被拒绝合作"};
    private int[] colors = new int[]{R.color.grey,
            R.color.colorPrimary,
            R.color.finished,
            R.color.grey,
            R.color.grey,
            R.color.blue,
            R.color.grey,
            R.color.working,
            R.color.grey};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders_mine, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mClasses = getArguments().getString("classes");
        initView();
        initData();
    }

    private void initData() {
        StringBuilder sbu = new StringBuilder(QUERY_ORDER);
        sbu.append("?o_c_u_id=").append(UserHelper.getInstance().getUser().getAccount_id())
                .append("&order_state=").append(mClasses)
                .append("&pageNumber=").append(mPageNumber)
                .append("&pageSingle=").append(mPageSingle);
        OkHttpUtils.get().url(sbu.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", "onError: " + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", mClasses + "onesponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == STATUS_SUCCESS) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            Orders orders = new Gson().fromJson(str, Orders.class);
                            mOrdersList.add(orders);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initView() {
        mLv = (ListView) findViewById(R.id.lv);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), OrdersDetilActivity.class)
                .putExtra("orders",mOrdersList.get(position)));
            }
        });
        mAdapter = new LVAdapter();
        mLv.setAdapter(mAdapter);
    }

    class LVAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mOrdersList.size();
        }

        @Override
        public Object getItem(int position) {
            return mOrdersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orders, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            Orders orders = mOrdersList.get(position);
            vh.mImg.setVisibility(View.GONE);
            if (orders.getMade_picture() != null) {
                String[] pics = orders.getMade_picture().split(",");
                for (int i = 0; i < pics.length; i++) {
                    if (pics[i].equals("null"))
                        continue;
                    vh.mImg.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(HOST+"/"+pics[i])
                            .into(vh.mImg);
                }
            }
            vh.mTime.setText(orders.getOrder_create_time());
            int status = Integer.parseInt(orders.getOrder_state());
            vh.mTvStatus.setText(STATUS[status]);
            vh.mTvStatus.setBackgroundColor(getResources().getColor(colors[status]));
            vh.mTvTitle.setText(orders.getMade_title());
            return convertView;
        }

        class ViewHolder {
            ImageView mImg;
            TextView mTvTitle;
            TextView mTvStatus;
            TextView mTime;

            public ViewHolder(View rootView) {
                this.mImg = (ImageView) rootView.findViewById(R.id.img);
                this.mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
                this.mTvStatus = (TextView) rootView.findViewById(R.id.tv_status);
                this.mTime = (TextView) rootView.findViewById(R.id.time);
            }

        }
    }
}
