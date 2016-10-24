package com.ddgj.dd.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;

import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2016/10/24.
 */
public class PublishBBSActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE = 2;
    private List<String> path;
    private TextView commitBBS;
    private ImageView hideKeyboard;
    private ImageView selectPic;

    @Override
    public void initView() {
        commitBBS = (TextView) findViewById(R.id.bbs_commit);
        commitBBS.setOnClickListener(this);
        hideKeyboard = (ImageView) findViewById(R.id.hide_keyboard);
        hideKeyboard.setOnClickListener(this);
        selectPic = (ImageView) findViewById(R.id.select_pic);
        selectPic.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_add);
        initView();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bbs_commit:
                showToastShort("发送一条论坛消息");
                toCommitBBS();
                break;
            case R.id.hide_keyboard:
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.select_pic:
                MultiImageSelector.create(PublishBBSActivity.this)
                        .start(PublishBBSActivity.this, REQUEST_IMAGE);
                break;
            default:

                break;
        }
    }
    private void toCommitBBS() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("title", String.valueOf("帖子标题"));
        params.put("cordcontent", String.valueOf("帖子内容"));
        params.put("bbs_type", String.valueOf("帖子类型"));
        params.put("user_id", String.valueOf("用户ID"));
        params.put("user_name", String.valueOf("用户姓名"));
        params.put("picture_id", String.valueOf("图片ID"));


        PostFormBuilder post = OkHttpUtils.post();

     /*   if (path!=null) {
            for (int i = 0; i < path.size(); i++) {
                file = FileUtil.scal(Uri.parse(path.get(i)), cacheDir);
                String s = "o_picture";
                post.addFile(s + i, file.getName(), file);

            }
        }*/
        post.url(NetWorkInterface.PUBLISH_BBS)
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
                        PublishBBSActivity.this.finish();
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                // 获取返回的图片列表
                path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                for (String p : path) {
                    int px = DensityUtil.dp2px(this, 60);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(px, px);
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    //addImageGroup.addView(imageView);
                    Glide.with(this).load(p).into(imageView);
                   // selectPic.setVisibility(View.GONE);
                }

            }
        }
    }


}
