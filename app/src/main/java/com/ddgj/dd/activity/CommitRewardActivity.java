package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;

public class CommitRewardActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private EditText mContent;
    private RadioButton mYes;
    private RadioButton mNo;
    private RadioGroup mSecret;
    private ImageView mAddImage;
    private LinearLayout mImages;
    private Button mCommit;
    private RewardInfo mReward;
    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mImagePath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_reward);
        initView();
    }

    protected void initView() {
        mReward = (RewardInfo) getIntent().getSerializableExtra("data");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContent = (EditText) findViewById(R.id.content);
        mYes = (RadioButton) findViewById(R.id.yes);
        mNo = (RadioButton) findViewById(R.id.no);
        mSecret = (RadioGroup) findViewById(R.id.secret);
        mAddImage = (ImageView) findViewById(R.id.add_image);
        mImages = (LinearLayout) findViewById(R.id.images);
        mCommit = (Button) findViewById(R.id.commit);

        mAddImage.setOnClickListener(this);
        mCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                if (submit()) {
                    commit();
                }
                break;
            case R.id.add_image:
                addImage();
                break;
        }
    }

    private void commit() {
        PostFormBuilder post = OkHttpUtils.post();
        if (!mImagePath.isEmpty()) {
            int i = 0;
            for (String path : mImagePath) {
                File file = FileUtil.compressBitmap(path, (float) (1024 * 1024 * 8));
                String s = "reward_task_picture";
                post.addFile(s + i++, file.getName(), file);
            }
        }
        final SweetAlertDialog dialog = showLoadingDialog("正在发送您的悬赏","");
        HashMap<String, String> params = new HashMap<>();
        params.put("reward_task_id",mReward.getReward_task_id());
        params.put("reward_task_content",mContent.getText().toString().trim());
        params.put("r_t_secrecy_state",mSecret.getCheckedRadioButtonId()==R.id.yes?"1":"0");
        post.url(NetWorkInterface.COMMIT_REWARD_ORDER).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("提交悬赏失败："+e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                logi("提交悬赏成功："+response);
                try {
                    if(new JSONObject(response).getInt("status")==0){
                        showToastShort("任务提交成功！");
                        setResult(SUCCESS);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                mImagePath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String p : mImagePath) {
                    int px = DensityUtil.dip2px(this, 60);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(px, px);
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    mImages.addView(imageView, 0);
                    Glide.with(this).load(p).into(imageView);
//                    mAddImg.setVisibility(View.GONE);
                }
            }
        }
    }

    private void addImage() {
        MultiImageSelector.create(this)
                .start(this, REQUEST_IMAGE);
    }

    private boolean submit() {
        String contentString = mContent.getText().toString().trim();
        if (TextUtils.isEmpty(contentString)) {
            Toast.makeText(this, "作品内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDailog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDailog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("")
                .setTitleText("是否放弃已编辑内容？")
                .setConfirmText("放弃")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                })
                .setCancelText("继续")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }
}
