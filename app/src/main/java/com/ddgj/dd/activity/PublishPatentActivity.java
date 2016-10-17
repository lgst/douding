package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2016/10/17.
 */

public class PublishPatentActivity extends BaseActivity implements View.OnClickListener {

    private Spinner spinnerPatentType;
    private String sPatentTyoeSpinner;
    private Spinner spinnerPatentCategory;
    private EditText patentName;
    private EditText patentIntro;
    private EditText patentInfor;
    private EditText patentUserName;
    private EditText patentUserEmail;
    private EditText patentUserPhone;
    private EditText patentNumber;
    private EditText patentEmpower;
    private EditText patentAssignmentPrice;
    private CheckBox assignmentCheck;
    private Button pickPic;
    private Button commitPatent;
    private ImageView backUp;
    private static final int REQUEST_IMAGE = 2;
    private ImageView selectPic;
    private String sPatentName;
    private String sPatentIntro;
    private String sPatentInfor;
    private String sPatentUserName;
    private String sPatentUserEmail;
    private String sPatentUserPhone;
    private String sPatentNumber;
    private String sPatentEmpower;
    private String sPatentAssignmentPrice;
    private String sPatentCategory;
    private boolean checked;
    private ArrayList<String> path;
    private File file;

    @Override
    public void initViews() {
        backUp = (ImageView) findViewById(R.id.backup);
        backUp.setOnClickListener(this);
        patentName = (EditText) findViewById(R.id.patent_name);
        patentIntro = (EditText) findViewById(R.id.patent_intro);
        patentInfor = (EditText) findViewById(R.id.patent_infor);
        patentUserName = (EditText) findViewById(R.id.patent_user_name);
        patentUserEmail = (EditText) findViewById(R.id.patent_user_email);
        patentUserPhone = (EditText) findViewById(R.id.patent_user_phone);
        patentNumber = (EditText) findViewById(R.id.patent_number);
        patentEmpower = (EditText) findViewById(R.id.patent_empower_price);
        patentAssignmentPrice = (EditText) findViewById(R.id.patent_assignment_price);
        assignmentCheck = (CheckBox) findViewById(R.id.assignment_checkBox);
        pickPic = (Button) findViewById(R.id.pick_pic);
        pickPic.setOnClickListener(this);
        commitPatent = (Button) findViewById(R.id.commit_patent);
        commitPatent.setOnClickListener(this);
        spinnerPatentType = (Spinner) findViewById(R.id.patent_type);
        spinnerPatentCategory = (Spinner) findViewById(R.id.category_spinner);
        selectPic = (ImageView) findViewById(R.id.select_pic);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_patent);
        initViews();
        initTypeSpinner();

    }

    private void initTypeSpinner() {
        String[] mItems = getResources().getStringArray(R.array.patent_type);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.textview_spinner_item, mItems);
        spinnerPatentType.setAdapter(spinnerAdapter);
        spinnerPatentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sPatentTyoeSpinner = String.valueOf(position);

                //Toast.makeText(PublishCreativeActivity.this, "你点击的是:"+position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backup:
                PublishPatentActivity.this.finish();
                break;
            case R.id.pick_pic:
                //this.startActivity(new Intent(this, CameraActivity.class).putExtra("pickPic",1));
                MultiImageSelector.create(PublishPatentActivity.this)
                        .start(PublishPatentActivity.this, REQUEST_IMAGE);
                break;
            case R.id.commit_patent:
                getAllInfor();
                toCommitIdea();
                break;
            default:
                break;
        }
    }

    /**
     * 提交所有文本信息
     */
    private void toCommitIdea() {
        if (check(sPatentName, sPatentIntro, sPatentInfor, sPatentUserName, sPatentUserEmail, sPatentUserPhone, sPatentNumber, sPatentEmpower, sPatentAssignmentPrice)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("patent_name", String.valueOf(sPatentName));
            params.put("patent_introduce", String.valueOf(sPatentIntro));
            params.put("patent_details", String.valueOf(sPatentInfor));
            params.put("patent_category", String.valueOf(sPatentCategory));
            params.put("patent_type", String.valueOf(sPatentTyoeSpinner));
            params.put("patent_number", String.valueOf(sPatentNumber));
            params.put("p_user_name", String.valueOf(sPatentUserName));
            params.put("p_user_contact", String.valueOf(sPatentUserPhone));
            params.put("p_user_email", sPatentUserEmail);
            params.put("p_user_address", "p_user_address");
            params.put("p_authorization_price", String.valueOf(sPatentEmpower));
            params.put("p_transfer_price", String.valueOf(sPatentAssignmentPrice));
            params.put("p_authorization_state", String.valueOf(checked));

            params.put("o_account_id", "o_account_id");
            params.put("o_nickname", "o_nickname");
            params.put("head_picture", "head_picture");


            file = new File(path.get(0));

            //this.file =  FileUtil.scal(Uri.parse(p));
            // Log.e("fabu1", this.file.getName()+ this.file.length()+"前文件后"+file2.getName()+file2.length());


            OkHttpUtils.post()
                    .addFile("patent_picture", file.getName(), file)
                    .url(NetWorkInterface.ADD_Patent)
                    .params(params).build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                            Log.e("fabu", e.getMessage() + " 失败id:" + id);
                            showToastLong("失败");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e("fabu", " 成功id:" + id);
                            showToastLong("成功");
                            PublishPatentActivity.this.finish();
                        }
                    });

        }
    }

    /**
     * 获取所有输入文本信息
     */
    private void getAllInfor() {
        sPatentName = patentName.getText().toString().trim();
        sPatentIntro = patentIntro.getText().toString().trim();
        sPatentInfor = patentInfor.getText().toString().trim();
        sPatentUserName = patentUserName.getText().toString().trim();
        sPatentUserEmail = patentUserEmail.getText().toString().trim();
        sPatentUserPhone = patentUserPhone.getText().toString().trim();
        sPatentNumber = patentNumber.getText().toString().trim();
        sPatentEmpower = patentEmpower.getText().toString().trim();
        sPatentAssignmentPrice = patentAssignmentPrice.getText().toString().trim();
        sPatentCategory = (String) this.spinnerPatentCategory.getSelectedItem();
        checked = assignmentCheck.isChecked();
        Log.e("getinfo", sPatentName +
                sPatentIntro +
                sPatentInfor +
                sPatentUserName +
                sPatentUserEmail +
                sPatentUserPhone +
                sPatentNumber +
                sPatentEmpower +
                sPatentAssignmentPrice +
                sPatentCategory +
                sPatentCategory+
                sPatentTyoeSpinner+
                checked);
    }

    private boolean check(String ideaname, String ideaintro, String idrainfor, String username, String userphone1, String userphone2, String userphone3, String userphone4, String userphone5) {
        if (ideaname.isEmpty() || ideaintro.isEmpty() || idrainfor.isEmpty() || username.isEmpty() || userphone1.isEmpty() || userphone2.isEmpty() || userphone3.isEmpty() || userphone4.isEmpty() || userphone5.isEmpty()) {
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
                    System.out.println(p + "");
                    Glide.with(this).load(p).into(selectPic);
                }

            }
        }
    }
}


