package com.ddgj.dd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.BBSCommentBean;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.easeui.EaseConstant;
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
 * Created by Administrator on 2016/10/27.
 */
public class BBSCommentActivity extends BaseActivity implements View.OnClickListener {
    private List<BBSCommentBean> bbsCommentBeanList=new ArrayList<BBSCommentBean>();
    private PullToRefreshListView pullToRefreshListView;
    private ImageView sendComment;
    private ImageView backup;
    private EditText etComment;
    private String etCommentText;
    private int mPageNumber = 1;
    private BBSCommentBean bbsCommentBean;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_comment);
        getBBSComment();
        initView();

    }

    @Override
    public void initView() {
        backup = (ImageView) findViewById(R.id.backup);
        backup.setOnClickListener(this);
        etComment = (EditText) findViewById(R.id.et_comment);

        sendComment = (ImageView) findViewById(R.id.send_comment);
        sendComment.setOnClickListener(this);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                bbsCommentBeanList.clear();
                mPageNumber=1;
                getBBSComment();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNumber++;
                getBBSComment();
            }
        });
        commentAdapter = new CommentAdapter(this);
        pullToRefreshListView.setAdapter(commentAdapter);
    }

    /**
     * 获取当前帖子的所有评论
     */
    private void getBBSComment() {
        Map<String, String> params = new HashMap<String, String>();
        Log.e("comment1",getIntent().getStringExtra("PostID"));
        params.put("reply_id",getIntent().getStringExtra("PostID"));
        params.put("pageNumber", String.valueOf(mPageNumber));
        params.put("pageSingle",String.valueOf("10"));
        OkHttpUtils.post().url(NetWorkInterface.GET_ALL_COMMENT_BBS).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pullToRefreshListView.onRefreshComplete();
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
                            bbsCommentBean = new Gson().fromJson(string, BBSCommentBean.class);
                            bbsCommentBeanList.add(bbsCommentBean);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                commentAdapter.notifyDataSetChanged();
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backup:
                finish();
                break;
            case R.id.send_comment:
                Log.e("comment1","send_comment");
                    toSendComment();
                break;

            default:
                break;
        }
    }

    /**
     * 发送论坛评论
     */
    private void toSendComment() {
        Log.e("comment1", "toSendComment");
        etCommentText = etComment.getText().toString().trim();
        if (etCommentText.isEmpty()) {
            showToastShort("请输入评论");
        } else {
            Map<String, String> params = new HashMap<String, String>();
            params.put("reply_id", getIntent().getStringExtra("PostID"));
            params.put("reply_type", "reply_type");
            params.put("content", etCommentText);
            params.put("from_id", UserHelper.getInstance().getUser().getAccount_id());
            params.put("to_id", "to_id");
            OkHttpUtils.post().url(NetWorkInterface.ADD_COMMENT_BBS).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    showToastShort("评论成功");
                    InputMethodManager imm = (InputMethodManager) getSystemService(BBSCommentActivity.this.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    etComment.setText("");
                    bbsCommentBeanList.clear();
                    getBBSComment();
                }
            });
        }
    }


    private class CommentAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public CommentAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return bbsCommentBeanList.size();
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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                //可以理解为从vlist获取view  之后把view返回给ListView
                convertView = mInflater.inflate(R.layout.comment_list_item, null);
                holder.username = (TextView)convertView.findViewById(R.id.username);
                holder.commentContent = (TextView)convertView.findViewById(R.id.comment_content);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                holder.headPic = (CircleImageView) convertView.findViewById(R.id.head_pic);
                holder.send = (ImageView) convertView.findViewById(R.id.send_message);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.username.setText(bbsCommentBeanList.get(position).getAccount());
            holder.commentContent.setText(bbsCommentBeanList.get(position).getContent());
            holder.time.setText(StringUtils.getDate(bbsCommentBeanList.get(position).getFollowcard_date()));
            Glide.with(BBSCommentActivity.this)
                    .load(NetWorkInterface.HOST + "/" + bbsCommentBeanList.get(position).getHead_picture())
                    .error(R.mipmap.ic_crop_original_grey600_48dp)
                    .placeholder(R.mipmap.ic_crop_original_grey600_48dp)
                    .thumbnail(0.1f)
                    .into(holder.headPic);
            if(UserHelper.getInstance().getUser()!=null)
            {
                if(UserHelper.getInstance().getUser().getAccount().equals(bbsCommentBeanList.get(position).getAccount()))
                    holder.send.setVisibility(View.INVISIBLE);
            }
            holder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(parent.getContext(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, bbsCommentBeanList.get(position).getAccount()));
                }
            });
            return convertView;
        }
    }
    //提取出来方便点
    public final class ViewHolder {
        public TextView username;
        public TextView commentContent;
        public TextView time;
        public CircleImageView  headPic;
        public ImageView send;

    }
}
