package com.ddgj.dd.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.CommentAdapter;
import com.ddgj.dd.bean.Comment;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
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

public class CommentListActivity extends BaseActivity {

    private PullToRefreshListView mList;
    private int pageSingle = 20;
    private int pageNumber = 1;
    private static final int UPDATE = 1;
    private static final int LOAD = 2;
    private List<Comment> comments = new ArrayList<Comment>();
    private CommentAdapter mAdapter;
    private EditText commentEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        initView();
        initData(LOAD);
    }

    private void initData(final int flag) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("topic_id", getIntent().getStringExtra("topic_id"));
        params.put("pageSingle", String.valueOf(pageSingle));
        params.put("pageNumber", String.valueOf(pageNumber));
        OkHttpUtils.post().url(NetWorkInterface.GET_COMMENT).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: " + e.getMessage());
                showToastShort("请求失败，请稍后重试！");
                mList.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        if (flag == LOAD)
                            comments.clear();
                        for (int i = 0; i < ja.length(); i++) {
                            Comment comment = new Gson().fromJson(ja.get(i).toString(), Comment.class);
                            comments.add(comment);
                        }
                        if (flag == LOAD) {
                            mAdapter = new CommentAdapter(comments);
                            mList.setAdapter(mAdapter);
                        } else {
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                        }
                        mList.onRefreshComplete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendCommentClick(View v) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        final String comment = commentEt.getText().toString();
        if (comment.isEmpty()) {
            showToastShort("请输入评论内容！");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("topic_id", getIntent().getStringExtra("topic_id"));
        params.put("topic_type", String.valueOf(getIntent().getIntExtra("classes", -1)));
        params.put("c_content", comment);
        params.put("from_u_id", UserHelper.getInstance().getUser().getAccount_id());
        OkHttpUtils.post().url(NetWorkInterface.ADD_COMMENT).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: 评论出错！" + e.getMessage());
                showToastShort("网络连接失败，请稍后重试！");
            }

            @Override
            public void onResponse(String response, int id) {
                ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                if (responseInfo.getStatus() == 0) {
                    Log.i(TAG, "onResponse: 评论成功！");
                    commentEt.setText(null);
                    showToastShort("评论成功！");
                } else {
                    showToastShort("评论失败，请稍后重试！");
                }
            }
        });
    }
    @Override
    public void initView() {
        mList = (PullToRefreshListView) findViewById(R.id.list);
        mList.setMode(PullToRefreshBase.Mode.BOTH);
        commentEt = (EditText) findViewById(R.id.et_comment);
        mList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNumber = 1;
                initData(LOAD);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNumber++;
                initData(UPDATE);
            }
        });
    }

    public void backClick(View v) {
        finish();
    }
}
