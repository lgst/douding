package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

import static com.ddgj.dd.R.id.city;


/**
 * Created by QinDaluzy on 2016/12/7/0007.
 * 采购发布页
 */

public class PublishPurchaseActivity extends BaseActivity implements View.OnClickListener, NetWorkInterface {

    private EditText pname;
    private EditText pnum;
    private EditText psize;
    private EditText pprice;
    private EditText pperson;
    private EditText pphonenum;
    private EditText paddress;
    private EditText premark;
    private Button commitpurchase;
    private String sPname;
    private String sPnum;
    private String sPprice;
    private String sPperson;
    private String sPphonenum;
    private String sPaddress;
    private String sPremark;
    private String sDescription;
    private ArrayList<String> path;
    private File file;
    private Toolbar mToolbar;
    private EditText mCity;
    private EditText mDescription;
    private Spinner mType;

    /**
     * 初始化控件
     */
    public void initView() {
        pname = (EditText) findViewById(R.id.purchase_name);
        pnum = (EditText) findViewById(R.id.purchase_num);
        pprice = (EditText) findViewById(R.id.purchase_price);
        pperson = (EditText) findViewById(R.id.purchase_person);
        pphonenum = (EditText) findViewById(R.id.phone_purchase);
        paddress = (EditText) findViewById(R.id.purchase_address);
        premark = (EditText) findViewById(R.id.purchase_remark);
        commitpurchase = (Button) findViewById(R.id.commit_purchase);
        commitpurchase.setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_blue);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDailog();
            }
        });
        mToolbar.setTitle("采购发布");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setBackgroundColor(Color.WHITE);
        mCity = (EditText) findViewById(city);
        mCity.setOnClickListener(this);
        mCity.setText(getSharedPreferences("city", MODE_PRIVATE).getString("city", ""));
        mDescription = (EditText) findViewById(R.id.description);
        mType = (Spinner) findViewById(R.id.type);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_purchase);
        initView();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit_purchase:
                toCommitIdea();
                break;
            case R.id.city:
                startActivityForResult(new Intent(this, CitySelecterActivity.class), 1);
                break;
        }
    }

    /*
        获取文本
     */
    private void getAllInfor() {
        sPname = pname.getText().toString().trim();
        sPnum = pnum.getText().toString().trim();
        sPprice = pprice.getText().toString().trim();
        sPperson = pperson.getText().toString().trim();
        sPphonenum = pphonenum.getText().toString().trim();
        sPaddress = paddress.getText().toString().trim();
        sPremark = premark.getText().toString().trim();
        sDescription = mDescription.getText().toString().trim();
    }

    private boolean check() {
        if (sPname.isEmpty() ||
                sPnum.isEmpty() ||
                sPperson.isEmpty() ||
                sPprice.isEmpty() ||
                sPphonenum.isEmpty() ||
                sPaddress.isEmpty() ||
                sDescription.isEmpty()) {
            showToastShort("请填写完整采购信息！");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS) {
            mCity.setText(data.getStringExtra("city"));
        }
    }

    private void toCommitIdea() {
        getAllInfor();
        if (!check())
            return;
        Map<String, String> params = new HashMap<String, String>();
        params.put("procurement_province", "");
        params.put("procurement_city", mCity.getText().toString());
        params.put("procurement_area", "");
        params.put("procurement_name", sPname);
        params.put("procurement_price", sPprice);
        params.put("procurement_person", sPperson);
        params.put("procurement_number", sPnum);
        params.put("procurement_phone", sPphonenum);
        params.put("procurement_address", sPaddress);
        params.put("procurement_remark", sPremark);
        params.put("p_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("procurement_classes", String.valueOf(mType.getSelectedItem()));
        params.put("procurement_describe", sDescription);
        final SweetAlertDialog sad = showLoadingDialog("正在提交您的购信息！", "请稍等...");
        OkHttpUtils.post().url(ADD_PROCUREMENT).params(params).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(PublishPurchaseActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        sad.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            logi(response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 0) {
                                Toast.makeText(PublishPurchaseActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PublishPurchaseActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                        } finally {
                            sad.dismiss();
                        }
                    }
                });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDailog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDailog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("")
                .setTitleText("是否放弃已编辑内容？")
                .setConfirmText("放弃")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
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
}
