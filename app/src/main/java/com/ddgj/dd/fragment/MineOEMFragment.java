package com.ddgj.dd.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.activity.OEMDetailActivity;
import com.ddgj.dd.adapter.OrderAdapter;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/20.
 */

public class MineOEMFragment extends BaseFragment implements NetWorkInterface {
    private SwipeMenuListView mplv;
    private LinearLayout mLoading;
    private List<Order> mOrders = new ArrayList<Order>();
    private OrderAdapter mAdapter;
    private BaseActivity activity;
    private SweetAlertDialog mDialog;
    private View mView;
    private boolean refresh;
    private int pageNumber = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        return inflater.inflate(R.layout.fragment_mine_patent, null);
    }

    @Override
    protected void initView() {
        mplv = (SwipeMenuListView) findViewById(R.id.list_view);
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Order order = mOrders.get(position);
                startActivity(new Intent(getActivity(), OEMDetailActivity.class)
                        .putExtra("id", order.getMade_id()));
            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.grey_lightE)));
                // set item width
                deleteItem.setWidth(DensityUtil.dip2px(getContext(), 90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete_grey600_48dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mplv.setMenuCreator(creator);
        mplv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // 删除
                        Log.i("lgst", "onMenuItemClick: " + "delete");
                        showDeleteDialog(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        mView = getActivity().getLayoutInflater().inflate(R.layout.item_listview_footer, null);
        mplv.addFooterView(mView);
        mplv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE)
                    refresh = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.i("lgst", "onScroll: "+totalItemCount);
                if (firstVisibleItem + visibleItemCount >= totalItemCount && refresh) {
                    //加载数据
                    pageNumber++;
                    initData();
                    refresh = false;
                }
            }
        });
        mAdapter = new OrderAdapter(mOrders);
        mplv.setAdapter(mAdapter);
        mplv.setEmptyView(findViewById(R.id.not_data));
    }

    /**
     * 删除提示
     */
    private void showDeleteDialog(final int position) {
        mDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
        mDialog.setTitleText("警告")
                .setContentText("删除后不可恢复，您真的要删除吗？")
                .setConfirmText("删除")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteData(position);
                    }
                })
                .setCancelText("取消")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 删除数据
     */
    private void deleteData(final int position) {
        Order order = mOrders.get(position);
        String id = order.getMade_id();
        OkHttpUtils.get().url(DELETE_ORDER + "?" + "made_id=" + id).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), "删除失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                mOrders.remove(position);
                mAdapter.notifyDataSetInvalidated();
                Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    /**
     * @param: flag：数据加载方式  LOAD：重新加载  UPDATE：加载更多
     * classes:分类  ALL：全部   NEW：最新   HOT：最热   MINE：我的
     */
    private void initData() {
        if (!activity.checkNetWork()) {
            activity.showToastNotNetWork();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(pageNumber));
        params.put("pageSingle", "10");
        params.put("made_differentiate", "1");
        params.put("m_a_id", UserHelper.getInstance().getUser().getAccount_id());
//        params.put("originality_differentiate",String.valueOf(0));
        new HttpHelper<Order>(getActivity(), Order.class)
                .getDatasPost(GET_MINE_ORDER, params, new DataCallback<Order>() {
                    @Override
                    public void Failed(Exception e) {
                        Log.e("lgst", "我的代工获取出错：" + e.getMessage());
                    }

                    @Override
                    public void Success(List<Order> datas) {
                        if (datas.size() < 10) {
                            mplv.removeFooterView(mView);
                        }
                        mOrders.addAll(datas);
                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
                            mLoading.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                });
//        OkHttpUtils.post().url(GET_MINE_ORDER).params(params).build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                activity.showToastNotNetWork();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                Log.i("lgst", response);
//                try {
//                    JSONObject jo = new JSONObject(response);
//                    int status = jo.getInt("status");
//                    if (status == STATUS_SUCCESS) {
//                        JSONArray ja = jo.getJSONArray("data");
//                        for (int i = 0; i < ja.length(); i++) {
//                            String patentStr = ja.getJSONObject(i).toString();
//                            Order order = new Gson().fromJson(patentStr, Order.class);
//                            mOrders.add(order);
//                        }
//                        mAdapter = new OrderAdapter(mOrders);
//                        mplv.setAdapter(mAdapter);
//                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
//                            mLoading.setVisibility(View.GONE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
