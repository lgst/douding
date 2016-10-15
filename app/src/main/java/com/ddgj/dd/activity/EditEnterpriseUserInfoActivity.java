package com.ddgj.dd.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditEnterpriseUserInfoActivity extends BaseActivity implements TextWatcher {
    /**
     * 企业名称
     */
    private EditText enterpriseName;
    /**
     * 企业规模
     */
    private EditText enterpriseScale;
    /**
     * 领域
     */
    private EditText enterpriseField;
    /**
     * 区域
     */
    private EditText enterpriseArea;
    /**
     * 地址
     */
    private EditText enterpriseAddress;
    /**
     * 邮箱
     */
    private EditText enterpriseEmail;
    /**
     * 联系人
     */
    private EditText enterpriseLinkName;
    /**
     * 联系方式
     */
    private EditText enterpriseContact;
    private boolean textChanged;
    private EnterpriseUser user;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==SUCCESS)
            {
                showToastShort("修改完成！");
                textChanged = false;
                setResult(SUCCESS);
            }else if(msg.what==FAILDE){
                showToastShort((String) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_enterprise_user_info);
        user = (EnterpriseUser) UserHelper.getInstance().getUser();
        initViews();
        initDatas();
    }

    private void initDatas() {
        enterpriseName.setText(user.getFacilitator_name());
        enterpriseScale.setText(user.getFacilitator_scale());
        enterpriseField.setText(user.getFacilitator_field());
        enterpriseArea.setText(user.getFacilitator_area());
        enterpriseAddress.setText(user.getFacilitator_address());
        enterpriseEmail.setText(user.getFacilitator_email());
        enterpriseLinkName.setText(user.getFacilitator_linkman());
        enterpriseContact.setText(user.getFacilitator_contact());

        enterpriseName.addTextChangedListener(this);
        enterpriseScale.addTextChangedListener(this);
        enterpriseField.addTextChangedListener(this);
        enterpriseArea.addTextChangedListener(this);
        enterpriseAddress.addTextChangedListener(this);
        enterpriseEmail.addTextChangedListener(this);
        enterpriseLinkName.addTextChangedListener(this);
        enterpriseContact.addTextChangedListener(this);
    }

    @Override
    public void initViews() {
        enterpriseName = (EditText) findViewById(R.id.enterprise_name);
        enterpriseScale = (EditText) findViewById(R.id.enterprise_scale);
        enterpriseField = (EditText) findViewById(R.id.enterprise_field);
        enterpriseArea = (EditText) findViewById(R.id.enterprise_area);
        enterpriseAddress = (EditText) findViewById(R.id.enterprise_address);
        enterpriseEmail = (EditText) findViewById(R.id.enterprise_email);
        enterpriseLinkName = (EditText) findViewById(R.id.enterprise_link_name);
        enterpriseContact = (EditText) findViewById(R.id.enterprise_contact);
    }

    public void backClick(View v) {
        showDailog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDailog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDailog() {
        if (!textChanged) {
            finish();
            return;
        }
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("")
                .setTitleText("是否放弃对用户信息的修改？")
                .setConfirmText("放弃")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        setResult(SUCCESS);
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

    public void saveClick(View v) {
        if (!textChanged) {
            showToastShort("您还未修改任何信息！");
            return;
        }
        final String name = enterpriseName.getText().toString().trim();
        final String scale = enterpriseScale.getText().toString().trim();
        final String field = enterpriseField.getText().toString().trim();
        final String area = enterpriseArea.getText().toString().trim();
        final String email = enterpriseEmail.getText().toString().trim();
        final String address = enterpriseAddress.getText().toString().trim();
        final String lickName = enterpriseLinkName.getText().toString().trim();
        final String contact = enterpriseContact.getText().toString().trim();
        OkHttpClient client = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder()
                .add("account_type", user.getAccount_type())
                .add("account_id", user.getAccount_id())
                .add("facilitator_name", name)
                .add("facilitator_field", field)
                .add("facilitator_scale", scale)
                .add("facilitator_area", area)
                .add("facilitator_address", address)
                .add("facilitator_contact", contact)
                .add("facilitator_linkman", lickName)
                .add("facilitator_email", email)
                .add("client_side", "app");
        Request request = new Request.Builder()
                .url(NetWorkInterface.CHANGE_USER_INFO)
                .post(builder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("lgst", request.body().toString() + e.getMessage());
                Message msg = new Message();
                msg.obj = e.getMessage();
                msg.what = FAILDE;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseContent = response.body().string();
                Log.e("lgst", responseContent);
                ResponseInfo responseInfo = new Gson().fromJson(responseContent, ResponseInfo.class);
                if (responseInfo.getStatus() == 0) {
                    handler.sendEmptyMessage(SUCCESS);
                    user.setFacilitator_name(name);
                    user.setFacilitator_field(field);
                    user.setFacilitator_scale(scale);
                    user.setFacilitator_area(area);
                    user.setFacilitator_address(address);
                    user.setFacilitator_contact(contact);
                    user.setFacilitator_linkman(lickName);
                    user.setFacilitator_email(email);
                    user.saveToSharedPreferences(EditEnterpriseUserInfoActivity.this);
                } else {
                    Message msg = new Message();
                    msg.what = FAILDE;
                    msg.obj = responseInfo.getMsg();
                    handler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
