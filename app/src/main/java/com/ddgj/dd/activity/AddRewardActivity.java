package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.PermissionUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;

public class AddRewardActivity extends BaseActivity implements NetWorkInterface, View.OnClickListener {

    private Toolbar mToolbar;
    private EditText mTitle;
    private EditText mPrice;
    private EditText mTime;
    private Spinner mTenderMod;
    private EditText mPhoneNumber;
    private TextView mMissionRequirementsTv;
    private EditText mMissionInfo;
    private TextView mRewardRelatedImagesTv;
    private ImageView mAddImg;
    private LinearLayout mAllPicLl;
    private TextView mRewardPromptTwoTv;
    private CheckBox mRewardAgree;
    private Button mCommitBtn;
    private boolean isAgreed;
    private User user;
    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mImagePath = new ArrayList<>();
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reward);
        user = UserHelper.getInstance().getUser();
        initView();
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setTitle("我要悬赏");
        mToolbar.setTitleTextColor(Color.parseColor("#014886"));
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDailog();
            }
        });
        mTitle = (EditText) findViewById(R.id.title);
        mPrice = (EditText) findViewById(R.id.price);
        mTime = (EditText) findViewById(R.id.time);
        mTenderMod = (Spinner) findViewById(R.id.tender);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mMissionInfo = (EditText) findViewById(R.id.mission_info);
        mUserName = (EditText) findViewById(R.id.user_name);
        mAddImg = (ImageView) findViewById(R.id.add_img);
        mAddImg.setOnClickListener(this);
        mAllPicLl = (LinearLayout) findViewById(R.id.all_pic_ll);
        mRewardAgree = (CheckBox) findViewById(R.id.reward_agree);
        mRewardAgree.setOnClickListener(this);
        mCommitBtn = (Button) findViewById(R.id.commit_btn);
        mCommitBtn.setOnClickListener(this);
        init();
    }

    private void init() {
        if (user instanceof EnterpriseUser) {
            mPhoneNumber.setText(((EnterpriseUser) user).getFacilitator_contact());
            mUserName.setText(((EnterpriseUser) user).getFacilitator_name());
        } else if (user instanceof PersonalUser) {
            mPhoneNumber.setText(((PersonalUser) user).getPhone_number());
            mUserName.setText(((PersonalUser) user).getUser_name());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reward_agree:
                isAgreed = mRewardAgree.isChecked();
                break;
            case R.id.add_img:
                if (PermissionUtils.requestAllPermissions(this, 201))
                    addImage();
                break;
            case R.id.commit_btn:
                if (!submit())
                    break;
                commit();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            addImage();
        }
    }

    private void commit() {
        PostFormBuilder post = OkHttpUtils.post();
        HashMap<String, String> params = new HashMap<>();
        if (!mImagePath.isEmpty()) {
            int i = 0;
            for (String path : mImagePath) {
                File file = FileUtil.compressBitmap(path, (float) (1024 * 1024 * 8));
                String s = "reward_picture";
                post.addFile(s + i++, file.getName(), file);
            }
        }else {
            params.put("reward_picture","");
        }
        final SweetAlertDialog dialog = showLoadingDialog("正在发送您的悬赏", "");
        params.put("reward_title", mTitle.getText().toString().trim());
        params.put("reward_price", mPrice.getText().toString().trim());
        params.put("reward_start_time", "");
        params.put("reward_cycle", mTime.getText().toString().trim());
        params.put("reward_pattern", (String) mTenderMod.getSelectedItem());
        params.put("reward_require", mMissionInfo.getText().toString().trim());
        params.put("reward_u_phone", mPhoneNumber.getText().toString().trim());
        params.put("reward_u_name", mUserName.getText().toString().trim());
        params.put("reward_u_id", user.getAccount_id());
        params.put("reward_type", String.valueOf(mTenderMod.getSelectedItem()));
        post.url(ADD_REWARD).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge("悬赏发布出错：" + e.getMessage());
                showToastNotNetWork();
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                logi(response);
                ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                if (responseInfo.getStatus() == 0) {
                    showToastShort("悬赏发布成功！");
                    dialog.dismiss();
//                    EventBus.getDefault().post(new BusEvent(BusEvent.REWARD));
                    finish();
                }
            }
        });
    }

    private void addImage() {
        MultiImageSelector.create(this)
                .start(this, REQUEST_IMAGE);
    }

    private boolean submit() {
        if (!isAgreed) {
            showToastShort("请勾选同意后提交！");
            return false;
        }
        String titleString = mTitle.getText().toString().trim();
        if (TextUtils.isEmpty(titleString)) {
            Toast.makeText(this, "标题不能为空不能为空", Toast.LENGTH_SHORT).show();
            mTitle.requestFocus();
            return false;
        }

        String priceString = mPrice.getText().toString().trim();
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "金额不能为空", Toast.LENGTH_SHORT).show();
            mPrice.requestFocus();
            return false;
        }

        String timeString = mTime.getText().toString().trim();
        if (TextUtils.isEmpty(timeString)) {
            Toast.makeText(this, "悬赏周期不能为空", Toast.LENGTH_SHORT).show();
            mTime.requestFocus();
            return false;
        }

        String number = mPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
            mPhoneNumber.requestFocus();
            return false;
        }

        String info = mMissionInfo.getText().toString().trim();
        if (TextUtils.isEmpty(info)) {
            Toast.makeText(this, "请认真描述您的任务要求，以便参加者更了解您的任务，做出更完美的作品。", Toast.LENGTH_SHORT).show();
            return false;
        }
        String name = mUserName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "联系人不能为空", Toast.LENGTH_SHORT).show();
            mUserName.requestFocus();
            return false;
        }
        return true;
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
                    mAllPicLl.addView(imageView, 0);
                    Glide.with(this).load(p).into(imageView);
//                    mAddImg.setVisibility(View.GONE);
                }
            }
        }
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
