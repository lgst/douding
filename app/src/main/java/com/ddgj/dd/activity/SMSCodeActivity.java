package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ddgj.dd.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.ddgj.dd.R.id.sms_code;

public class SMSCodeActivity extends BaseActivity implements TextWatcher {
    private boolean cancle = false;
    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    cancle = true;
                    Log.i(TAG, "afterEvent: 验证成功！");
                    SMSSDK.unregisterEventHandler(eh);//反注册短信回调
                    setResult(SUCCESS, new Intent().putExtra("phone", phone));
//                    startActivity(new Intent(SMSCodeActivity.this,RegisterActivity.class).putExtra("phone",phone));
                    finish();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.i(TAG, "afterEvent: 获取验证码成功！");
                    mSendCode.setEnabled(false);
//                    startTimer();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };
    private String code;
    private EditText mPhone;

    private void startTimer() {
        mSendCode.setEnabled(false);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                int count = 60;
                while (!cancle) {
                    publishProgress(count);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (count-- <= 0) {
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                mSendCode.setText("重新获取" + "(" + values[0] + "s)");
                Log.i(TAG, "onProgressUpdate: " + values[0]);
                if (((Integer) values[0]) == 0) {
                    mSendCode.setText("获取验证码");
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mSendCode.setEnabled(true);
            }
        };
        asyncTask.execute();
    }

    private String country = "+86";
    private String phone = "18165157887";
    private EditText mSmsCode;
    private Button mSendCode;
    private Button mSubmit;

    @Override
    public void initView() {
        mSmsCode = (EditText) findViewById(sms_code);
        mSmsCode.addTextChangedListener(this);
        mSendCode = (Button) findViewById(R.id.send_code);
        mSubmit = (Button) findViewById(R.id.submit);
        mPhone = (EditText) findViewById(R.id.phone);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smscode);
        initView();
        SMSSDK.initSDK(this, "1851542c409e4", "af95d295ff297f0764bc4955b769148e");
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    public void submitClick(View v) {
        code = mSmsCode.getText().toString();
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    public void backClick(View v) {
        cancle = true;
        finish();
    }

    public void sendCodeClick(View v) {
        if (!checkNetWork()) {//网络检查
            showToastNotNetWork();
            return;
        }
        phone = mPhone.getText().toString();
        if (phone.matches("(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}")) {
            SMSSDK.getVerificationCode(country, phone);
            startTimer();
            Log.i(TAG, "sendCodeClick: ");
        } else {
            showToastShort("手机号码有误！");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (count >= 4) {
//            mSubmit.setEnabled(true);
//        } else {
//            mSubmit.setEnabled(false);
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}