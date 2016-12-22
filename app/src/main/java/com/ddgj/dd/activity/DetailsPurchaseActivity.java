package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.Purchase;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/7/0007.
 */

public class DetailsPurchaseActivity extends BaseActivity implements View.OnClickListener {
    private TextView purtitle;
    private TextView purdetails;
    private TextView purnum;
    private TextView purprice;
    private TextView purdaxiao;
    private TextView purperson;
    private TextView purphone;
    private TextView puraddress;
    private TextView purremark;
    private Button chengjie;

    private String purchase_id;
    private Toolbar mToolbar;

    protected void initView() {
        purtitle = (TextView) findViewById(R.id.detail_pur_title);
        purdetails = (TextView) findViewById(R.id.detail_pur_description);
        purnum = (TextView) findViewById(R.id.tv_de_pur_num);
        purprice = (TextView) findViewById(R.id.tv_de_pur_price);
        purdaxiao = (TextView) findViewById(R.id.tv_de_pur_daxiao);
        purperson = (TextView) findViewById(R.id.tv_de_pur_person);
        purphone = (TextView) findViewById(R.id.tv_de_pur_phone);
        puraddress = (TextView) findViewById(R.id.tv_de_pur_address);
        purremark = (TextView) findViewById(R.id.tv_de_pur_remark);

        chengjie = (Button) findViewById(R.id.commit_purchase_chengjie);
        chengjie.setOnClickListener(this);
        purchase_id = getIntent().getStringExtra("purchaseId");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("采购详情");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_purchase);
        initView();
        initData();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.commit_purchase_chengjie:
                Intent intent = new Intent();
                intent.setClass(DetailsPurchaseActivity.this, CustomActivity.class);
                intent.putExtra("purchaseId", purchase_id);
                startActivity(intent);
                break;
        }

    }

    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("procurement_id", String.valueOf(purchase_id));
        OkHttpUtils.post().url(NetWorkInterface.HOST + "/procurementById.do").params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("kk", "kk" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                JsonData(response);
            }
        });
    }

    private void JsonData(String response) {
        JSONObject js = null;
        try {
            js = new JSONObject(response);
            Purchase mPur = new Gson().fromJson(js.getString("data"), Purchase.class);

            purtitle.setText(mPur.getProcurement_name());
            purdetails.setText(mPur.getProcurement_describe());
            purnum.setText(mPur.getProcurement_number());
            purprice.setText(mPur.getProcurement_price());
            purphone.setText(mPur.getProcurement_phone());
            puraddress.setText(mPur.getProcurement_address());
            purremark.setText(mPur.getProcurement_remark());
            User user = UserHelper.getInstance().getUser();
            if (user != null && !mPur.getP_u_id().equals(user.getAccount_id()))
                chengjie.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
