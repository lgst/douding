package com.ddgj.dd.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/8/0008.
 */
public class CustomActivity extends BaseActivity implements View.OnClickListener,NetWorkInterface {
    private EditText customer;
    private EditText phone;
    private EditText allMoney;
    private EditText b_date;
    private EditText remark;
    private Button commit;

    private String sCustomer;
    private String sPhone;
    private String sAllMoney;
    private String sB_date;
    private String sRemark;
    private String purchase_id;
    private String user_id;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_costom);
        purchase_id = getIntent().getStringExtra("purchaseId");
        //user_id =  UserHelper.getInstance().getUser().getAccount_id();
        initView();
    }

    /**
     * 初始化控件
     */
    protected void initView() {
        customer = (EditText) this.findViewById(R.id.et_custom_customer);
        phone = (EditText) this.findViewById(R.id.et_custom_phone);
        allMoney = (EditText) this.findViewById(R.id.et_custom_allMoney);
        b_date = (EditText) this.findViewById(R.id.et_custom_date);
        remark = (EditText) this.findViewById(R.id.et_custom_remark);
        commit = (Button) this.findViewById(R.id.btn_custom_commit);
        commit.setOnClickListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("承接");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setBackgroundColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void getEdittext() {
        sCustomer = customer.getText().toString().trim();
        sPhone = phone.getText().toString().trim();
        sAllMoney = allMoney.getText().toString().trim();
        sB_date = b_date.getText().toString().trim();
        sRemark = remark.getText().toString().trim();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_custom_commit:
                initData();
                break;
        }
    }

    private void initData() {
        getEdittext();
        Map<String, String> params = new HashMap<String, String>();
        params.put("continue_person", String.valueOf(sCustomer));
        params.put("continue_phone", String.valueOf(sPhone));
        params.put("continue_remark", String.valueOf(sRemark));
        params.put("procurement_type", String.valueOf(""));
        params.put("c_u_id", String.valueOf(user_id));
        params.put("procurement_id", String.valueOf(purchase_id));
        final SweetAlertDialog sad = showLoadingDialog("正在提交","请稍等...");
        OkHttpUtils.post().url(CONTINUE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("kk", "kk" + e.toString());
                Toast.makeText(CustomActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                sad.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {

                JSONObject js = null;
                try {
                    js = new JSONObject(response);
                    if (js.getString("status").equals("0")) {
                        Toast.makeText(CustomActivity.this, "承接成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CustomActivity.this, "承接失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    sad.dismiss();
                }
            }
        });
    }
}
