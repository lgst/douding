package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.PostBean;
import com.ddgj.dd.bean.PostContentBean;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.EaseConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

/**
 * Created by Administrator on 2016/10/22.
 */
public class PlateDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TextView postSubTitle;
    private Toolbar mToolbar;
    private TextView postPublishData;
    private CircleImageView headPic;
    private TextView userAmount;
    private LinearLayout postContent;
    private PostBean postBean;
    private String head_picture;
    private TextView postContentTv;
    private LinearLayout postContentAll;
    private TextView tvComment;
    private ImageView chat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_deatils);
        chat = (ImageView) findViewById(R.id.chat);
        tvComment = (TextView) findViewById(R.id.tv_comment);
        postSubTitle = (TextView) findViewById(R.id.post_sub_title);
        postPublishData = (TextView) findViewById(R.id.post_publish_data);
        userAmount = (TextView) findViewById(R.id.user_amount);
        headPic = (CircleImageView) findViewById(R.id.head_pic);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        Intent intent = getIntent();
        String postId = intent.getStringExtra("post_id");
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", postId);
        PostFormBuilder post = OkHttpUtils.post();
        post.url(NetWorkInterface.GET_POST_DETAIL)
                .params(params).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e("fabubbs", e.getMessage() + " 失败id:" + id);
                        showToastLong("请求失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("fabubbs", " 成功id:" + id);
//                        showToastLong("成功");
                        Log.e("detail", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            if (status == STATUS_SUCCESS) {
                                String string = jo.getString("data");
                                postBean = new Gson().fromJson(string, PostBean.class);
                                User user = UserHelper.getInstance().getUser();
                                if (user != null) {
                                    if (!user.getAccount().equals(postBean.getAccount())) {
                                        chat.setVisibility(View.VISIBLE);
                                    }
                                }
                                initView();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void initView() {
        if (postBean != null && !this.isFinishing()) {
            postPublishData.setText(StringUtils.getDate(postBean.getSend_date()));
            mToolbar.setTitle(postBean.getTitle());
            postSubTitle.setText(postBean.getTitle());
            userAmount.setText(postBean.getAccount());
            postContent = (LinearLayout) findViewById(R.id.post_content);
            postContentTv = (TextView) findViewById(R.id.post_content_tv);
            tvComment.setOnClickListener(this);
            Log.e(TAG, "initView: " + NetWorkInterface.HOST + "/" + postBean.getHead_picture());
            Glide.with(PlateDetailsActivity.this)
                    .load(NetWorkInterface.HOST + "/" + postBean.getHead_picture())
                    .into(headPic);
            Log.e("toJson", postBean.getCordcontent());

            ArrayList<PostContentBean> beanArrayList = new Gson()
                    .fromJson(postBean.getCordcontent(), new TypeToken<ArrayList<PostContentBean>>() {
                    }.getType());
            for (int i = 0; i < beanArrayList.size(); i++) {
                PostContentBean postContentBean = beanArrayList.get(i);
                String content = postContentBean.getContent();
                int order = postContentBean.getOrder();
                Log.e("toJson", "content:" + content + "order:" + order);
            }
            // postContentTv.setText(postBean.getCordcontent());
            addAllContent(beanArrayList);
        }


    }

    /**
     * 动态添加所有的view
     */
    private void addAllContent(ArrayList<PostContentBean> beanArrayList) {
        final String[] imgs = postBean.getPicture_id().split("\\,");
        for (int i = 0; i < imgs.length; i++) {
            Log.e("toJson", "所有图片的链接" + imgs[i]);
            // NetWorkInterface.HOST + "/" + imgs[i]
        }

        postContentAll = (LinearLayout) findViewById(R.id.post_content);
        int j = 0;
        for (int i = 0; i < beanArrayList.size(); i++) {
            PostContentBean postContentBean = beanArrayList.get(i);
            String content = postContentBean.getContent();
            if (content.startsWith("**\n\n这里是图片")) {

                Log.e("toJson", "这里是图片" + imgs[j]);
                //int px = DensityUtil.dip2px(this, 60);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final int finalJ = j;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(PlateDetailsActivity.this,PhotoScale.class).putExtra("photoURL",imgs[finalJ]));
                    }
                });
                postContentAll.addView(imageView);
                Glide.with(this)
                        .load(NetWorkInterface.HOST + "/" + imgs[j])
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                j++;
            } else {
                int px = DensityUtil.dip2px(this, 60);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(this);
                textView.setLayoutParams(layoutParams);
                textView.setText(content);
                textView.setTextIsSelectable(true);
                postContentAll.addView(textView);
            }

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment:
                if (UserHelper.getInstance().isLogined()) {
                    startActivity(new Intent(this, BBSCommentActivity.class).putExtra("PostID", postBean.getId()));
                } else {
                    showToastShort("请先登录！");
                    startActivity(new Intent(this, LoginActivity.class).putExtra("flag", LoginActivity.BACK));
                }
                break;
            default:
                break;
        }
    }

    public void sendMessageClick(View v) {
        startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, postBean.getAccount()));
    }
}
