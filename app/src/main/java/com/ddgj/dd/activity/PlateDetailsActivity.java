package com.ddgj.dd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.activity.PublishBBSActivity;
import com.ddgj.dd.bean.PostBean;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

/**
 * Created by Administrator on 2016/10/22.
 */
public class PlateDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backUp;
    private TextView postTitle;
    private TextView postSubTitle;
    private TextView postPublishData;
    private CircleImageView headPic;
    private TextView userAmount;
    private LinearLayout postContent;
    private PostBean postBean;
    private String head_picture;
    private TextView postContentTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_deatils);
        initData();
    }

    private void initData() {
        Intent intent=getIntent();
        String postId = intent.getStringExtra("post_id");
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",postId);
        PostFormBuilder post = OkHttpUtils.post();
        post.url(NetWorkInterface.GET_POST_DETAIL)
                .params(params).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e("fabubbs", e.getMessage() + " 失败id:" + id);
                        showToastLong("失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("fabubbs", " 成功id:" + id);
                        showToastLong("成功");
                       Log.e("detail",response);
                       Log.e("detail",response);
                       Log.e("detail",response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            if (status==STATUS_SUCCESS){
                                    String string = jo.getString("data");
                                    postBean = new Gson().fromJson(string, PostBean.class);

                                }
                            initView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
    @Override
    public void initView() {
        if (postBean!=null){
            backUp = (ImageView) findViewById(R.id.backup);
            backUp.setOnClickListener(this);
            postTitle = (TextView) findViewById(R.id.post_title);
            postTitle.setText(postBean.getTitle());
            postSubTitle = (TextView) findViewById(R.id.post_sub_title);
            postSubTitle.setText(postBean.getTitle());
            postPublishData = (TextView) findViewById(R.id.post_publish_data);
            postPublishData.setText(StringUtils.getDate(postBean.getSend_date()));
            userAmount = (TextView) findViewById(R.id.user_amount);
            userAmount.setText(postBean.getAccount());
            headPic = (CircleImageView) findViewById(R.id.head_pic);
            Glide.with(this)
                    .load(NetWorkInterface.HOST + "/" + postBean.getHead_picture())
                    .error(R.mipmap.ic_crop_original_grey600_48dp)
                    .placeholder(R.mipmap.ic_crop_original_grey600_48dp)
                    .thumbnail(0.1f)
                    .into(headPic);
            postContent = (LinearLayout) findViewById(R.id.post_content);
            postContentTv = (TextView) findViewById(R.id.post_content_tv);
            postContentTv.setText(postBean.getCordcontent());
            addAllContent();
        }


    }

    /**
     * 动态添加所有的view
     */
    private void addAllContent() {
        String cordcontent = postBean.getCordcontent();
        String picture_id = postBean.getPicture_id();
        dealAllContent(cordcontent,picture_id);
        TextView textView = new TextView(this);
        //textView.setText();

    }

    /**
     * 處理圖文魂牌
     */
    private void dealAllContent(String cordcontent,String picture_id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backup:
                finish();
                break;
            default:
                break;
        }
    }
}
