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
import com.ddgj.dd.activity.WebActivity;
import com.ddgj.dd.adapter.OriginalityPLVAdapter;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

import static com.ddgj.dd.R.id.list_view;

/**
 * Created by Administrator on 2016/10/20.
 */

public class MineOriginalityFragment extends BaseFragment implements NetWorkInterface {
    private LinearLayout mLoading;
    private List<Originality> mOriginalitys = new ArrayList<Originality>();
    private OriginalityPLVAdapter mAdapter;
    private BaseActivity activity;
    private SwipeMenuListView mListView;
    private SweetAlertDialog mDialog;
    private int pageNumber = 1;
    private boolean refresh;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        return inflater.inflate(R.layout.fragment_mine_originality, null);
    }

    @Override
    protected void initView() {
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mListView = (SwipeMenuListView) findViewById(list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("lgst", "onItemClick: " + "start");
                final Originality originality = mOriginalitys.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("originality_id", originality.getOriginality_id());
                OkHttpUtils.post().url(GET_ORIGINALITY_DETAILS).params(params).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lgst", "获取创意详情页失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                        if (responseInfo.getStatus() == STATUS_SUCCESS) {
                            String url = responseInfo.getData();
                            Log.e("lgst", url);
                            startActivity(new Intent(activity, WebActivity.class)
                                    .putExtra("title", originality.getOriginality_name())
                                    .putExtra("url", HOST + url));
                        }
                    }
                });
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

// set creator
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // 删除
//                        Log.i("lgst", "onMenuItemClick: " + "delete");
                        showDeleteDialog(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.i("lgst", "onScrollStateChanged: " + scrollState);
                if (scrollState == SCROLL_STATE_IDLE)
                    refresh = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.i("lgst", "onScroll: " + firstVisibleItem + " visib:" + visibleItemCount + " total:" + totalItemCount);
                if (firstVisibleItem + visibleItemCount >= totalItemCount && refresh) {
                    //加载数据
                    pageNumber++;
                    initData();
                    refresh = false;
                }
            }
        });
        mAdapter = new OriginalityPLVAdapter(activity, mOriginalitys);
        mListView.setAdapter(mAdapter);
        mView = getActivity().getLayoutInflater().inflate(R.layout.item_listview_footer, null);
        mListView.addFooterView(mView);
        mListView.setEmptyView(findViewById(R.id.not_data));
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
        Originality ori = mOriginalitys.get(position);
        String id = ori.getOriginality_id();
        OkHttpUtils.get().url(DELETE_ORIGINALITY + "?" + "originality_id=" + id).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), "删除失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                mOriginalitys.remove(position);
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
//        if (mOriginalitys.size() > 0) {
//            return;
//        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf(pageNumber));
        params.put("pageSingle", "10");
        params.put("o_account_id", UserHelper.getInstance().getUser().getAccount_id());
//        params.put("originality_differentiate",String.valueOf(0));
        new HttpHelper<Originality>(getActivity(), Originality.class)
                .getDatasPost(GET_MINE_ORIGINALITY, params, new DataCallback<Originality>() {
                    @Override
                    public void Failed(Exception e) {

                    }

                    @Override
                    public void Success(List<Originality> datas) {
                        if (datas.isEmpty() || datas.size() < 10)
                            mListView.removeFooterView(mView);
                        mOriginalitys.addAll(datas);
                        mAdapter.notifyDataSetChanged();
                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
                            mLoading.setVisibility(View.GONE);
                    }
                });
//        OkHttpUtils.post().url(GET_MINE_ORIGINALITY).params(params).build().execute(new StringCallback() {
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
//                            Originality originality = new Gson().fromJson(patentStr, Originality.class);
//                            mOriginalitys.add(originality);
//                        }
//                        if (mLoading.getVisibility() == View.VISIBLE)//关闭加载数据页面
//                            mLoading.setVisibility(View.GONE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }
}
