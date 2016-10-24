package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends BaseActivity {

    private EditText userName;
    private EditText passWord;
    private EditText confirm;
    private Spinner questions;
    private EditText answer;
    private RadioGroup userType;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    Intent intent = new Intent();
                    intent.putExtra("username", (String) msg.obj);
                    setResult(SUCCESS, intent);
                    RegisterActivity.this.showToastShort("注册完成！");
                    RegisterActivity.this.finish();
                    break;
                case FAILDE:
                    RegisterActivity.this.showToastShort((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    public void initView() {
        userName = (EditText) findViewById(R.id.user_name);
        passWord = (EditText) findViewById(R.id.pass_word);
        confirm = (EditText) findViewById(R.id.confirm);
        questions = (Spinner) findViewById(R.id.question_spinner);
        answer = (EditText) findViewById(R.id.answer);
        userType = (RadioGroup) findViewById(R.id.user_type);
    }

    public void backClick(View v) {
        finish();
    }

    public void registerClick(View v) {
        if(!checkNetWork())
        {
            showToastNotNetWork();
            return;
        }
        //获取账号
        final String username = userName.getText().toString().trim();
        //获取密码
        String password = passWord.getText().toString().trim();
        //获取确认密码
        String confim = this.confirm.getText().toString().trim();
        //获取密保问题
        String question = (String) this.questions.getSelectedItem();
        //获取密保答案
        String answer = this.answer.getText().toString().trim();
        //获取用户类型
        int userType = this.userType.getCheckedRadioButtonId() == R.id.personal ? 0 : 1;
        if (check(username, password, confim, answer)) {
            //输入检查通过，开始提交注册
            //注册中圆形进度对话框
            final SweetAlertDialog dialog = showLoadingDialog("注册中...","");
            OkHttpClient client = new OkHttpClient();
            FormEncodingBuilder build = new FormEncodingBuilder();
            build.add("account", username)
                    .add("password", password)
                    .add("answer", answer)
                    .add("account_type", String.valueOf(userType))
                    .add("question", question)
                    .add("client_side","app");
//            Log.i("lgst", "username:" + username + " answer:" + answer + " account_type:" + userType + " question:" + question + " answer:" + answer);
            Request request = new Request.Builder()
                    .url(NetWorkInterface.REGISTER)
                    .post(build.build())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("lgst", "注册失败：" + e.getMessage());
                    Message msg = new Message();
                    msg.what = FAILDE;
                    msg.obj = "网络异常！";
                    handler.sendMessage(msg);
                    dialog.dismiss();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    dialog.dismiss();
                    Message msg = new Message();
                    String responseContent = response.body().string();
                    Log.i("lgst", responseContent);
                    Gson gson = new Gson();
                    ResponseInfo responseInfo = gson.fromJson(responseContent, ResponseInfo.class);
                    if (responseInfo.getStatus() == 0) {
                        msg.what = SUCCESS;
                        msg.obj = username;
                        handler.sendMessage(msg);
                    } else if (responseInfo.getStatus() == NetWorkInterface.STATUS_FAILED) {
                        msg.what = FAILDE;
                        msg.obj = responseInfo.getMsg();
                        handler.sendMessage(msg);
                    }
                }
            });
        }
    }

    private boolean check(String username, String password, String confirm, String answer) {
        if (username.isEmpty()) {
            showToastShort(getResources().getString(R.string.please_input_username));
            userName.requestFocus();
            return false;
        }
        if(!username.matches("([0-9]|[A-Za-z]|[_])+"))
        {
            showToastShort("用户名只能由字母、数字、下划线组成！");
            userName.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            showToastShort(getResources().getString(R.string.pwd_so_short));
            passWord.requestFocus();
            return false;
        }
        if (confirm.length() < 6) {
            showToastShort(getResources().getString(R.string.confirm_so_short));
            this.confirm.requestFocus();
            return false;
        }
        if (!password.equals(confirm)) {
            showToastShort(getResources().getString(R.string.password_not_equals_confirm));
            this.passWord.setText(null);
            this.confirm.setText(null);
            this.passWord.requestFocus();
            return false;
        }
        if (answer.isEmpty()) {
            showToastShort(getResources().getString(R.string.please_input_answer));
            this.answer.requestFocus();
            return false;
        }
        return true;
    }
}
