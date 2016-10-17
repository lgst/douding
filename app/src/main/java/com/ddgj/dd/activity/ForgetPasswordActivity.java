package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class ForgetPasswordActivity extends BaseActivity {
    private EditText userName;
    private EditText passWord;
    private EditText confirm;
    private Spinner questions;
    private EditText answer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                showToastShort(getResources().getString(R.string.forget_password_success));
                setResult(SUCCESS, new Intent().putExtra("username", (String) msg.obj));
            } else {
                showToastShort((String) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initViews();
    }

    public void backClick(View v) {
        finish();
    }

    public void forgetPasswordClick(View v) {
        if (!checkNetWork()) {
            showToastNotNetWork();
            return;
        }
        final String username = userName.getText().toString().trim();
        String password = passWord.getText().toString().trim();
        String confim = this.confirm.getText().toString().trim();
        String answer = this.answer.getText().toString().trim();
        String question = (String) this.questions.getSelectedItem();
        if (check(username, password, confim, answer)) {
            //找回密码
            OkHttpClient client = new OkHttpClient();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            builder.add("account", username)
                    .add("password", password)
                    .add("question", question)
                    .add("client_side", "app")
                    .add("answer", answer);
            Request request = new Request.Builder()
                    .url(NetWorkInterface.FORGET_PASSWORD)
                    .post(builder.build())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("lgst", "找回密码失败：" + e.getMessage());
                    handler.sendEmptyMessage(FAILDE);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseContent = response.body().string();
//                    Log.i("lgst", responseContent);
                    ResponseInfo responseInfo = new Gson().fromJson(responseContent, ResponseInfo.class);
                    Message msg = new Message();
                    if (responseInfo.getStatus() == NetWorkInterface.STATUS_SUCCESS) {
                        msg.what = SUCCESS;
                        msg.obj = username;
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

    @Override
    public void initViews() {
        userName = (EditText) findViewById(R.id.user_name);
        passWord = (EditText) findViewById(R.id.pass_word);
        confirm = (EditText) findViewById(R.id.confirm);
        questions = (Spinner) findViewById(R.id.question_spinner);
        answer = (EditText) findViewById(R.id.answer);
    }

    private boolean check(String username, String password, String confirm, String answer) {
        if (username.length() < 1) {
            showToastShort(getResources().getString(R.string.please_input_username));
            passWord.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            showToastShort(getResources().getString(R.string.please_input_true_pass_word));
            passWord.requestFocus();
            return false;
        }
        if (confirm.length() < 6) {
            showToastShort(getResources().getString(R.string.please_input_true_confirm_pass_word));
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
