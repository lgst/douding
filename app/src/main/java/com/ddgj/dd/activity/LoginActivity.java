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
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**用户登录界面*/
public class LoginActivity extends BaseActivity {
    public static final String BACK = "back";
    private EditText usernaemEt;
    private EditText pwdEt;
    private static final int REQUEST_CODE = 101;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    showToastShort("登录成功！");
                    if(getIntent().getStringExtra("flag").equals(BACK))
                    {
                        MainActivity.update=true;
                        UserHelper.getInstance().setLogined(true);
//                        UserHelper.getInstance().getUser().saveToSharedPreferences(LoginActivity.this);
                        UserHelper.getInstance().loadUserInfo();

                        finish();
                    }else {
                    }
                    break;
                case FAILDE:
                    showToastShort((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UserHelper.getInstance().isLogined()){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void initViews() {
        usernaemEt = (EditText) findViewById(R.id.act_login_et_user_name);
        pwdEt = (EditText) findViewById(R.id.act_login_et_pwd);
    }

    /**
     * 登录按钮点击事件
     */
    public void loginClick(View v) {
        if(!checkNetWork())
        {
            showToastNotNetWork();
            return ;
        }
        String usernameContent = usernaemEt.getText().toString().trim();
        String pwdContent = pwdEt.getText().toString().trim();
        if (checkInput(usernameContent, pwdContent)) {
            final SweetAlertDialog dialog = showLoadingDialog("登录中...","");
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
                            dialog.dismiss();
                    String responseContent = response.body().string();
                    Log.i("lgst", responseContent);
                    ResponseInfo responseInfo = null;
                    try {
                        responseInfo = JsonUtils.getResponse(responseContent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    if (responseInfo.getStatus() == NetWorkInterface.STATUS_SUCCESS) {
                        try {
                            Object user = JsonUtils.getUser(responseInfo.getData());
                            if(user instanceof EnterpriseUser)
                            {//企业用户
                                ((EnterpriseUser) user).saveToSharedPreferences(LoginActivity.this);
                            }else{//个人用户
                                ((PersonalUser)user).saveToSharedPreferences(LoginActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == SUCCESS) {
            //注册成功，自动填充用户名，光标设置到密码框
            usernaemEt.setText(data.getStringExtra("username"));
            pwdEt.requestFocus();
        }
    }

    /**
     * 忘记密码点击事件
     */
    public void forgetPasswordClick(View v) {
        startActivityForResult(new Intent(this, ForgetPasswordActivity.class), REQUEST_CODE);
//        startActivity(new Intent(this,UpdatePasswordActivity.class));
    }

    public void backClick(View v) {
        finish();
    }

}
