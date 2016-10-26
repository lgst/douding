package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.JsonUtils;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 用户登录界面
 * flag:back
 */
public class LoginActivity extends BaseActivity implements NetWorkInterface {
    public static final String BACK = "back";
    private static final int REGISTER_CODE = 102;
    private static final int FORGET_CODE = 103;
    private static final int FORGET_SMS_CODE = 104;
    private static final int REGISTER_SMS_CODE = 105;
    private EditText usernaemEt;
    private EditText pwdEt;
    private String phone = "18165157887";
    public static String userName;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    if (getIntent().getStringExtra("flag").equals(BACK)) {
                        MainActivity.update = true;
                        UserHelper.getInstance().setLogined(true);
//                        UserHelper.getInstance().getUser().saveToSharedPreferences(LoginActivity.this);
                        UserHelper.getInstance().loadUserInfo();
                        showToastShort((String) msg.obj);
                        dialog.dismiss();
                        finish();
                    } else {
                    }
                    break;
                case FAILDE:
                    showToastShort((String) msg.obj);
                    dialog.dismiss();
                    break;
            }
        }
    };
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserHelper.getInstance().isLogined()) {
            startActivity(new Intent(this, MainActivity.class));
        }
        usernaemEt.setText(userName);
    }

    public void initView() {
        usernaemEt = (EditText) findViewById(R.id.act_login_et_user_name);
        pwdEt = (EditText) findViewById(R.id.act_login_et_pwd);
    }

    /**
     * 登录按钮点击事件
     */
    public void loginClick(View v) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        final String usernameContent = usernaemEt.getText().toString().trim();
        final String pwdContent = pwdEt.getText().toString().trim();
        if (checkInput(usernameContent, pwdContent)) {
            dialog = showLoadingDialog("登录中...", "");
            OkHttpClient client = new OkHttpClient();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            builder.add("account", usernameContent)
                    .add("password", pwdContent);
            Request request = new Request.Builder()
                    .url(NetWorkInterface.LOGIN)
                    .post(builder.build()).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("lgst", "登录出错：" + e.getMessage());
                    Message msg = new Message();
                    msg.what = FAILDE;
                    msg.obj = "网络异常！";
                    handler.sendMessage(msg);
                    dialog.dismiss();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseContent = response.body().string();
                    Log.i("lgst", responseContent);
                    ResponseInfo responseInfo = null;
                    try {
                        responseInfo = JsonUtils.getResponse(responseContent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final Message msg = new Message();
                    if (responseInfo.getStatus() == NetWorkInterface.STATUS_SUCCESS) {
                        try {
                            final Object user = JsonUtils.getUser(responseInfo.getData());
                            if (user instanceof EnterpriseUser) {//企业用户
                                ((EnterpriseUser) user).saveToSharedPreferences(LoginActivity.this);
                            } else {//个人用户
                                ((PersonalUser) user).saveToSharedPreferences(LoginActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //登录环信
                        final ResponseInfo finalResponseInfo = responseInfo;
                        EMClient.getInstance().login(usernameContent, pwdContent, new EMCallBack() {//回调
                            @Override
                            public void onSuccess() {
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                Log.i("main", "登录聊天服务器成功！");
                                msg.what = SUCCESS;
                                msg.obj = "登录成功！";
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onProgress(int progress, String status) {
                            }

                            @Override
                            public void onError(int code, String message) {
                                Log.i("main", "登录聊天服务器失败！" + message);
                                msg.what = FAILDE;
                                msg.obj = "登录失败";
                                handler.sendMessage(msg);
                            }
                        });
                    } else {
                        msg.what = FAILDE;
                        msg.obj = responseInfo.getMsg();
                        handler.sendMessage(msg);
                    }
                }
            });
        }
    }

    /**
     * 输入检查
     */
    private boolean checkInput(String username, String pwd) {
        if (username.isEmpty()) {
            showToastShort(getResources().getString(R.string.please_input_username));
            return false;
        }
        if (pwd.length() < 6) {
            showToastShort(getResources().getString(R.string.pwd_so_short));
            return false;
        }
        return true;
    }

    /**
     * 注册点击事件
     */
    public void registerClick(View v) {
//        短信验证
        startActivityForResult(new Intent(this, SMSCodeActivity.class), REGISTER_SMS_CODE);
//        startActivityForResult(new Intent(this, RegisterActivity.class).putExtra("phone", phone), REGISTER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            phone = data.getStringExtra("phone");
        if (requestCode == REGISTER_SMS_CODE && resultCode == SUCCESS) {
//            注册短信验证成功，跳转到注册页面，带上手机号
            startActivityForResult(new Intent(this, RegisterActivity.class).putExtra("phone", phone), REGISTER_CODE);
            showToastShort("手机验证成功");
        } else if (requestCode == FORGET_SMS_CODE && resultCode == SUCCESS) {
//            找回密码短信验证成功，跳转到找回密码界面，带上手机号码
            startActivityForResult(new Intent(this, ForgetPasswordActivity.class).putExtra("phone", phone), FORGET_CODE);
        } else if (requestCode == REGISTER_CODE && resultCode == SUCCESS) {
            //注册成功，自动填充用户名，光标设置到密码框
            usernaemEt.setText(data.getStringExtra("username"));
            pwdEt.requestFocus();
        } else if (requestCode == FORGET_CODE && resultCode == SUCCESS) {
//            找回密码成功
            usernaemEt.setText(data.getStringExtra("username"));
            pwdEt.requestFocus();
        }
    }

    /**
     * 忘记密码点击事件
     */
    public void forgetPasswordClick(View v) {
//        短信验证
        startActivityForResult(new Intent(this, SMSCodeActivity.class), FORGET_SMS_CODE);
//        startActivity(new Intent(this,UpdatePasswordActivity.class));
    }

    public void backClick(View v) {
        finish();
    }

}
