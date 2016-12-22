package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.TenderInfo;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;

/**
 * 应征招标
 */
public class JoinTenderActivity extends BaseActivity implements NetWorkInterface, View.OnClickListener {

    private Toolbar toolbar;
    private EditText money;
    private EditText detail;
    private LinearLayout images;
    private EditText name;
    private EditText phone_number;
    private TextView tender_count;
    private Button commit;
    private User mUser;
    private ArrayList<String> mImagePath = new ArrayList<>();
    private ImageView mAddImage;
    private static final int REQUEST_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_tender);
        initView();
    }

    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        money = (EditText) findViewById(R.id.money);
        detail = (EditText) findViewById(R.id.detail);
        images = (LinearLayout) findViewById(R.id.images);
        images.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        phone_number = (EditText) findViewById(R.id.phone_number);
        tender_count = (TextView) findViewById(R.id.tender_count);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
        mUser = UserHelper.getInstance().getUser();
        if (mUser instanceof EnterpriseUser) {
            name.setText(((EnterpriseUser) mUser).getFacilitator_name());
            phone_number.setText(((EnterpriseUser) mUser).getFacilitator_contact());
        } else if (mUser instanceof PersonalUser) {
            name.setText(((PersonalUser) mUser).getUser_name());
            phone_number.setText(((PersonalUser) mUser).getPhone_number());
        }
        mAddImage = (ImageView) findViewById(R.id.add_image);
        mAddImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                if (submit())
                    commit();
                break;
            case R.id.add_image:
                addImage();
                break;
        }
    }

    private void addImage() {
        MultiImageSelector.create(this)
                .start(this, REQUEST_IMAGE);
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
                    images.addView(imageView, 0);
                    Glide.with(this).load(p).into(imageView);
                }
            }
        }
    }

    private void commit() {
        final SweetAlertDialog sad = showLoadingDialog("正在提交！", "请稍等...");
        sad.setCancelable(false);
        sad.show();
        TenderInfo tenderInfo = (TenderInfo) getIntent().getSerializableExtra("data");
        PostFormBuilder post = OkHttpUtils.post();
        if (!mImagePath.isEmpty()) {
            int i = 0;
            for (String path : mImagePath) {
                File file = FileUtil.compressBitmap(path, (float) (1024 * 1024 * 8));
                String s = "tender_picture";
                post.addFile(s + i++, file.getName(), file);
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("invite_t_id", tenderInfo.getInvite_t_id());
        params.put("tender_id",getIntent().getStringExtra("id"));
        params.put("tender_content", detail.getText().toString().trim());
        params.put("tender_u_name", name.getText().toString().trim());
        params.put("tender_u_phone", phone_number.getText().toString().trim());
        params.put("tender_u_success_num", "0");
        params.put("tender_price",money.getText().toString().trim());
        params.put("invite_u_id", UserHelper.getInstance().getUser().getAccount_id());
        post.url(COMMIT_TENDER_ORDER).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loge(e.getMessage());
                sad.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                logi(response);
                try {
                    sad.dismiss();
                    if (new JSONObject(response).getInt("status") == 0) {
                        showToastShort("提交成功");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean submit() {
        String moneyString = money.getText().toString().trim();
        if (TextUtils.isEmpty(moneyString)) {
            Toast.makeText(this, "任务预算不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        String detailString = detail.getText().toString().trim();
        if (TextUtils.isEmpty(detailString)) {
            Toast.makeText(this, "请填写应征描述！", Toast.LENGTH_SHORT).show();
            return false;
        }

        String nameString = name.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "请填写姓名！", Toast.LENGTH_SHORT).show();
            return false;
        }

        String number = phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "请填写联系方式！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
