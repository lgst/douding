package com.ddgj.dd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class OEMAddActivity extends BaseActivity implements View.OnClickListener {

    private EditText productName;
    private EditText orderTitle;
    private EditText orderPrice;
    private EditText orderNumber;
    private EditText orderDate;
    private EditText orderSpecifications;
    private EditText productIntro;
    private EditText productInfor;
    private EditText orderUserName;
    private EditText orderUserPhone;
    private EditText orderUserEmail;
    private Spinner madeType;
    private Button pickPic;
    private Button commitOrder;
    private ImageView selectPic;
    private ImageView backUp;
    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> path;
    private String sProductName;
    private String sOrderTitle;
    private String sOrderPrice;
    private String sOrderNumber;
    private String sOrderDate;
    private String sOrderSpecifications;
    private String sProductIntro;
    private String sProductInfor;
    private String sOrderUserName;
    private String sOrderUserPhone;
    private String sOrderUserEmail;
    private String sMadeType;
    private File file;
    private RadioButton personalMade;
    private RadioButton entrustMade;
    private boolean personalMadeChecked;
    private boolean entrustMadeChecked;
    private EditText orderUserAddress;
    private Spinner madeState;
    private String sMadeStateSpinner;
    private String sOrderUserAddress;
    private LinearLayout addImageGroup;
    private SweetAlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);
        initViews();
        initMadeStateSpinner();

    }
    private void initMadeStateSpinner() {
        String[] mItems = getResources().getStringArray(R.array.made_state);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.textview_spinner_item, mItems);

        madeState.setAdapter(spinnerAdapter);
        madeState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sMadeStateSpinner = String.valueOf(position);

                //Toast.makeText(PublishCreativeActivity.this, "你点击的是:"+position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void initViews() {
        backUp = (ImageView) findViewById(R.id.backup);
        backUp.setOnClickListener(this);
        productName = (EditText) findViewById(R.id.product_name);
        productName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        orderTitle = (EditText) findViewById(R.id.order_title);
        orderPrice = (EditText) findViewById(R.id.order_price);
        orderNumber = (EditText) findViewById(R.id.order_number);
        orderDate = (EditText) findViewById(R.id.order_date);
        orderSpecifications = (EditText) findViewById(R.id.order_Specifications);
        productIntro = (EditText) findViewById(R.id.product_intro);
        productInfor = (EditText) findViewById(R.id.product_infor);
        orderUserName = (EditText) findViewById(R.id.order_user_name);
        orderUserPhone = (EditText) findViewById(R.id.order_user_phone);
        orderUserEmail = (EditText) findViewById(R.id.order_user_email);
        orderUserAddress = (EditText) findViewById(R.id.order_user_address);
        madeType = (Spinner) findViewById(R.id.made_type_spinner);
        madeState = (Spinner) findViewById(R.id.made_state);
        pickPic = (Button) findViewById(R.id.pick_pic);
        pickPic.setOnClickListener(this);
        commitOrder = (Button) findViewById(R.id.commit_order);
        commitOrder.setOnClickListener(this);
        selectPic = (ImageView) findViewById(R.id.select_pic);

        personalMade = (RadioButton) findViewById(R.id.personal_made);
        entrustMade = (RadioButton) findViewById(R.id.entrust_made);

        //添加图片
        addImageGroup = (LinearLayout) findViewById(R.id.all_pic);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backup:
                finish();
                break;
            case R.id.pick_pic:
                //this.startActivity(new Intent(this, CameraActivity.class).putExtra("pickPic",1));
                MultiImageSelector.create(OEMAddActivity.this)
                        .start(OEMAddActivity.this, REQUEST_IMAGE);
                break;
            case R.id.commit_order:
                getAllInfor();
                toCommitIdea();
                break;
            default:
                break;
        }
    }

    /**
     * 提交定制信息
     */
    private void toCommitIdea() {
        if (check( sProductName,
                sOrderTitle ,
                sOrderPrice ,
                sOrderNumber ,
                sOrderDate ,
                sOrderSpecifications,
                sProductIntro ,
                sProductInfor ,
                sOrderUserName ,
                sOrderUserPhone,
                sOrderUserEmail ,
                sOrderUserAddress ,
                sMadeType )) {
            dialog = showLoadingDialog("", "正在发送您的定制");
            Map<String, String> params = new HashMap<String, String>();
            params.put("made_name", String.valueOf(sProductName));
            params.put("made_title", String.valueOf(sOrderTitle));
            params.put("made_type_id", String.valueOf(sMadeType));
            params.put("made_price", String.valueOf(sOrderPrice));
            params.put("made_amount", String.valueOf(sOrderNumber));
            params.put("made_cycle", String.valueOf(sOrderDate));
            params.put("made_specifications", String.valueOf(sOrderSpecifications));
            params.put("made_describe", String.valueOf(sProductIntro));
            params.put("made_note", sProductInfor);
            params.put("made_u_name", sOrderUserName);
            params.put("made_u_contact", String.valueOf(sOrderUserPhone));
            params.put("made_u_email", String.valueOf(sOrderUserEmail));
            params.put("made_u_address", String.valueOf(sOrderUserAddress));
            params.put("made_state", sMadeStateSpinner);
            params.put("m_a_id", "m_a_id");
            params.put("head_picture", "head_picture");


            //file = new File(path.get(0));

            //file =  FileUtil.scal(Uri.parse(path.get(0)));
            // Log.e("fabu1", this.file.getName()+ this.file.length()+"前文件后"+file2.getName()+file2.length());


            PostFormBuilder post = OkHttpUtils.post();
            for (int i = 0; i < path.size(); i++) {
                file = FileUtil.scal(Uri.parse(path.get(i)));
                String s="made_picture";
                post.addFile(s+i, file.getName(), file);
            }
            post.url(NetWorkInterface.ADD_Order)
                    .params(params).build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                            Log.e("fabu", e.getMessage() + " 失败id:" + id);
                            showToastLong("失败");
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e("fabu", " 成功id:" + id);
                            showToastLong("成功");
                            OEMAddActivity.this.finish();
                            dialog.dismiss();

                        }
                    });

        }
    }

    /**
     * 获取定制信息
     */
    private void getAllInfor() {
        sProductName = productName.getText().toString().trim();
        sOrderTitle = orderTitle.getText().toString().trim();
        sOrderPrice = orderPrice.getText().toString().trim();
        sOrderNumber = orderNumber.getText().toString().trim();
        sOrderDate = orderDate.getText().toString().trim();
        sOrderSpecifications = orderSpecifications.getText().toString().trim();
        sProductIntro = productIntro.getText().toString().trim();
        sProductInfor = productInfor.getText().toString().trim();
        sOrderUserName = orderUserName.getText().toString().trim();
        sOrderUserPhone = orderUserPhone.getText().toString().trim();
        sOrderUserEmail = orderUserEmail.getText().toString().trim();
        sOrderUserAddress = orderUserAddress.getText().toString().trim();
        sMadeType = (String) this.madeType.getSelectedItem();
        personalMadeChecked = personalMade.isChecked();
        entrustMadeChecked = entrustMade.isChecked();
    }

    /**
     * 檢查數據完成
     * @param ideaname
     * @param ideaintro
     * @param idrainfor
     * @param username
     * @param userphone1
     * @param userphone2
     * @param userphone3
     * @param userphone4
     * @param userphone5
     * @return
     */
    private boolean check(String ideaname,
                          String ideaintro,
                          String idrainfor,
                          String username,
                          String userphone1,
                          String userphone2,
                          String userphone3,
                          String userphone4,
                          String userphone5,
                          String userphone6,
                          String userphone9,
                          String userphone7,
                          String userphone8) {
        if (ideaname.isEmpty() || ideaintro.isEmpty()
                || idrainfor.isEmpty()
                || username.isEmpty()
                || userphone1.isEmpty()
                || userphone2.isEmpty()
                || userphone3.isEmpty()
                || userphone4.isEmpty()
                || userphone5.isEmpty()
                || userphone6.isEmpty()
                || userphone7.isEmpty()
                || userphone9.isEmpty()
                || userphone8.isEmpty()) {
            showToastShort("请输入完成信息");
            return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                for (String p : path) {
                    int px = DensityUtil.dp2px(this, 60);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(px, px);
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    addImageGroup.addView(imageView);
                    Glide.with(this).load(p).into(imageView);
                    selectPic.setVisibility(View.GONE);
                }

            }
        }
    }
}
