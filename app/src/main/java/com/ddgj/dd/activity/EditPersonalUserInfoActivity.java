package com.ddgj.dd.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.StringUtils;
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
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditPersonalUserInfoActivity extends BaseActivity implements TextWatcher {

    private EditText name;
    private EditText nickName;
    private RadioGroup gender;
    private EditText phoneNumber;
    private EditText email;
    private EditText address;
    private TextView age;
    private PersonalUser user = (PersonalUser) UserHelper.getInstance().getUser();
    private boolean textChanged;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                setResult(SUCCESS);
                showToastShort("修改完成！");
                textChanged = false;
            } else if (msg.what == FAILDE) {
                showToastShort((String)msg.obj);
            } else {
                showToastShort("网络状况不佳！修改失败！");
            }
        }
    };
    private String genderStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_user_info);
        initViews();
    }

    @Override
    public void initViews() {
        name = (EditText) findViewById(R.id.name);
        nickName = (EditText) findViewById(R.id.nick_name);
        gender = (RadioGroup) findViewById(R.id.gender);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        age = (TextView) findViewById(R.id.age);

        initDatas();
        name.addTextChangedListener(this);
        nickName.addTextChangedListener(this);
        phoneNumber.addTextChangedListener(this);
        email.addTextChangedListener(this);
        address.addTextChangedListener(this);
        age.addTextChangedListener(this);
    }

    private void initDatas() {
        name.setText(user.getUser_name());
        nickName.setText(user.getNickname());
        phoneNumber.setText(user.getPhone_number());
        email.setText(user.getUser_email());
        address.setText(user.getUser_address());
        //设置年龄
        if (user.getUser_age().isEmpty()) {
            age.setText("");
            age.setTag("");
        } else {
            age.setText(String.valueOf(StringUtils.getAge(user.getUser_age())));
            age.setTag(user.getUser_age());
        }
        //设置性别
        if (user.getUser_sex().equals("男")) {
            ((RadioButton) gender.getChildAt(1)).setChecked(true);
            genderStr = "男";
        } else if (user.getUser_sex().equals("女")) {
            ((RadioButton) gender.getChildAt(2)).setChecked(true);
            genderStr = "女";
        } else {
            ((RadioButton) gender.getChildAt(0)).setChecked(true);
            genderStr = "保密";
        }
        //设置性别切换监听
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (gender.getCheckedRadioButtonId()) {
                    case R.id.boy:
                        genderStr = "男";
                        break;
                    case R.id.girl:
                        genderStr = "女";
                        break;
                    case R.id.secret:
                        genderStr = "保密";
                        break;
                }
                textChanged = true;
            }
        });

    }

    public void saveClick(View v) {
        if (!textChanged) {
            showToastShort("您还未修改任何信息！");
            return;
        }
        final String nameStr = name.getText().toString().trim();
        final String nickNameStr = nickName.getText().toString().trim();
        final String phoneStr = phoneNumber.getText().toString().trim();
        final String emailStr = email.getText().toString().trim();
        final String addressStr = address.getText().toString().trim();
        final String ageStr = (String) age.getTag();

        OkHttpClient client = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder()
                .add("account_type", user.getAccount_type())
                .add("account_id", user.getAccount_id())
                .add("user_name", nameStr)
                .add("nickname", nickNameStr)
                .add("user_sex", genderStr)
                .add("user_age", ageStr)
                .add("phone_number", phoneStr)
                .add("client_side", "app")
                .add("user_email", emailStr)
                .add("user_address", addressStr);
        Request request = new Request.Builder().url(NetWorkInterface.CHANGE_USER_INFO).post(builder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("lgst", "修改个人用户信息失败：" + e.getMessage());
                handler.sendEmptyMessage(FAILDE);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseContent = response.body().string();
                Log.i("lgst", responseContent);
                ResponseInfo responseInfo = new Gson().fromJson(responseContent, ResponseInfo.class);
                if (responseInfo.getStatus() == 0) {
                    user.setUser_name(nameStr);
                    user.setNickname(nickNameStr);
                    user.setUser_sex(genderStr);
                    user.setUser_age(ageStr);
                    user.setPhone_number(phoneStr);
                    user.setUser_email(emailStr);
                    user.setUser_address(addressStr);
                    user.saveToSharedPreferences(EditPersonalUserInfoActivity.this);
                    handler.sendEmptyMessage(SUCCESS);
                } else {

                    Message msg = new Message();
                    msg.what = FAILDE;
                    msg.obj = responseInfo.getMsg();
                    handler.sendMessage(msg);
                }
            }
        });

    }

    public void backClick(View v) {
        showDailog();
    }

    public void ageClick(final View v) {
        Calendar calendar = Calendar.getInstance();
        if (user.getUser_age().isEmpty()) {
            calendar.setTime(new Date(System.currentTimeMillis()));
        } else {
            String[] date = user.getUser_age().split("-");
            int y = Integer.parseInt(date[0]);
            int m = Integer.parseInt(date[1]);
            int d = Integer.parseInt(date[2]);
            calendar.set(y, m, d);
        }
        DatePickerDialog dilog = new DatePickerDialog(this,
                android.R.style.Theme_Material_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        Log.i("lgst", date);
                        age.setText(String.valueOf(StringUtils.getAge(date)));
                        age.setTag(date);
                    }
                }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
        dilog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && textChanged) {
            showDailog();
            return false;
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
