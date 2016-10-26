package com.ddgj.dd.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ddgj.dd.R;
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

/**
 * 密码修改
 */
public class UpdatePasswordActivity extends BaseActivity {
    public static final int CHANGED = 200;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirm;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                showToastShort(getResources().getString(R.string.update_password_success));
                MainActivity.update = true;//修改密码成功，更新 我的 页面数据
                UserHelper.getInstance().logout();//修改密码成功执行登出操作
               // EMClient.getInstance().logout(true);
                setResult(CHANGED);//返回修改密码成功
                finish();
            } else {
                if (msg.obj == null) {
                    showToastShort(getResources().getString(R.string.update_password_faild));
                } else {
                    showToastShort((String) msg.obj);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initView();
    }

    @Override
    public void initView() {
        oldPassword = (EditText) findViewById(R.id.old_pass_word);
        newPassword = (EditText) findViewById(R.id.new_pass_word);
        confirm = (EditText) findViewById(R.id.confirm);
    }

    /**
     * 输入检查
     */
    private boolean checkInput(String username, String oldpassword, String newpassword, String confirm) {
        if (username.isEmpty()) {
            showToastShort(getResources().getString(R.string.please_input_username));
            return false;
        }
        if (oldpassword.length() < 6) {
            showToastShort(getResources().getString(R.string.please_input_old_pass_word));
            return false;
        }
        if (newpassword.length() < 6) {
            showToastShort(getResources().getString(R.string.please_input_new_pass_word));
            return false;
        }
        if (confirm.length() < 6) {
            showToastShort(getResources().getString(R.string.confirm_so_short));
            return false;
        }
        if (!newpassword.equals(confirm)) {
            showToastShort(getResources().getString(R.string.password_not_equals_confirm));
            return false;
        }
        return true;
    }

    public void backClick(View v) {
        finish();
    }

    /**
     * 点击修改密码事件
     */
    public void updatePasswordClick(View v) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        String username = getIntent().getStringExtra("user_id");
//        Log.i(TAG, "updatePasswordClick: username:"+username);
        String oldpassword = oldPassword.getText().toString().trim();
        String newpassword = newPassword.getText().toString().trim();
        String confirm = this.confirm.getText().toString().trim();
        if (checkInput(username, oldpassword, newpassword, confirm)) {
            OkHttpClient client = new OkHttpClient();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            builder.add("account", username)
                    .add("password", oldpassword)
                    .add("newPws", newpassword);
            Request request = new Request.Builder()
                    .url(NetWorkInterface.UPDATE_PASSWORD)
                    .post(builder.build()).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("lgst", e.getMessage());
                    handler.sendEmptyMessage(FAILDE);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Message msg = new Message();
                    String respose = response.body().string();
                    Log.i("lgst", respose);
                    ResponseInfo responseInfo = new Gson().fromJson(respose, ResponseInfo.class);
                    if (responseInfo.getStatus() == NetWorkInterface.STATUS_SUCCESS) {
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        msg.what = FAILDE;
                        msg.obj = responseInfo.getMsg();
                        handler.sendMessage(msg);
                    }
                }
            });
        }
    }
}
