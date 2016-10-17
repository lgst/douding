package com.ddgj.dd.activity;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;

import com.ddgj.dd.util.net.NetWorkInterface;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2016/10/13.
 */

public class PublishCreativeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backUp;
    private EditText editName;
    private EditText editIntro;
    private EditText editInfor;
    private EditText userNmae;
    private EditText userPhone;
    private Button pickPic;
    private Button selectMode;
    private Button commitIdea;
    private String sEditName;
    private String sEditIntro;
    private String sEditInfor;
    private String sEditUserName;
    private String sEditUserPhone;
    private EditText userEmail;
    private Button pickType;
    private String sEditUserEmail;
    private Spinner typeSpinner;
    private Spinner modeSpinner;
    private String sTypeSpinner;
    private String sModeSpinner;
    private InputStream is;

    private static final int REQUEST_IMAGE = 2;
    private ImageView selectPic;
    private List<String> path;
    private File file;
    private File file1;
    private File file2;


    @Override
    public void initViews() {
        backUp = (ImageView) findViewById(R.id.backup);
        backUp.setOnClickListener(this);
        editName = (EditText) findViewById(R.id.edit_name);
        editIntro = (EditText) findViewById(R.id.edit_intro);
        editInfor = (EditText) findViewById(R.id.edit_infor);
        userNmae = (EditText) findViewById(R.id.idea_user_name);
        userEmail = (EditText) findViewById(R.id.idea_user_email);
        userPhone = (EditText) findViewById(R.id.idea_user_phone);
        pickPic = (Button) findViewById(R.id.pick_pic);
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        pickPic.setOnClickListener(this);
        modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        commitIdea = (Button) findViewById(R.id.commit_idea);
        commitIdea.setOnClickListener(this);
        selectPic = (ImageView) findViewById(R.id.select_pic);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_creative);
        initViews();
        initModeSpinner();
    }

    private void initModeSpinner() {
        String[] mItems = getResources().getStringArray(R.array.secrecyType);
        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,R.layout.textview_spinner_item,mItems);
        modeSpinner.setAdapter(spinnerAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sModeSpinner = String.valueOf(position);

                //Toast.makeText(PublishCreativeActivity.this, "你点击的是:"+position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backup:
                PublishCreativeActivity.this.finish();
                break;
            case R.id.pick_pic:
                //this.startActivity(new Intent(this, CameraActivity.class).putExtra("pickPic",1));
                MultiImageSelector.create(PublishCreativeActivity.this)
                        .start(PublishCreativeActivity.this, REQUEST_IMAGE);
                break;
            case R.id.type_spinner:
                break;
            case R.id.mode_spinner:
                break;
            case R.id.commit_idea:
                getAllInfor();
                toCommitIdea();
                break;
            default:
                break;
        }
    }



    /**
     * 上传创意信息
     */
    private void toCommitIdea() {
        if (check(sEditName, sEditIntro, sEditInfor, sEditUserName, sEditUserPhone)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("originality_name", String.valueOf(sEditName));
            params.put("originality_introduce", String.valueOf(sEditIntro));
            params.put("originality_details", String.valueOf(sEditInfor));
            params.put("originality_type", String.valueOf(sTypeSpinner));
            params.put("o_user_name", String.valueOf(sEditUserName));
            params.put("o_user_contact", String.valueOf(sEditUserPhone));
            params.put("o_user_email", String.valueOf(sEditUserEmail));
            params.put("o_secrecy_type", String.valueOf(sModeSpinner));
            params.put("o_originality_address", "o_originality_address");
            params.put("o_account_id", "o_account_id");
            params.put("o_nickname", "o_nickname");
            params.put("head_picture", "head_picture");
            params.put("originality_differentiate", "0");

                 file = new File(path.get(0));
                file1 = new File(path.get(1));
                file2 = new File(path.get(2));
                //this.file =  FileUtil.scal(Uri.parse(p));
               // Log.e("fabu1", this.file.getName()+ this.file.length()+"前文件后"+file2.getName()+file2.length());



            OkHttpUtils.post()
                    .addFile("o_picture1",file.getName(),file)
 .addFile("o_picture2",file1.getName(),file1)
 .addFile("o_picture3",file2.getName(),file2)

                    .url(NetWorkInterface.ADD_IDEA)
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
                }
            });

        }
    }

    /**
     * 获取用户所有的输入信息
     */
    private void getAllInfor() {
        sEditName = this.editName.getText().toString().trim();
        sEditIntro = this.editIntro.getText().toString().trim();
        sEditInfor = this.editInfor.getText().toString().trim();
        sEditUserName = this.userNmae.getText().toString().trim();
        sEditUserEmail = this.userEmail.getText().toString().trim();
        sEditUserPhone = this.userPhone.getText().toString().trim();
        sTypeSpinner = (String) this.typeSpinner.getSelectedItem();
        int id = this.typeSpinner.getSelectedItemPosition();
        System.out.println(id+"模式id");
        System.out.println(id+"模式id");
        System.out.println(id+"模式id");
        Log.e("douding","shuju模式id"+id+sEditName+sEditIntro+sEditInfor+sEditUserName+sEditUserEmail+sEditUserPhone+sTypeSpinner+sModeSpinner);


    }

    /**
     * 检查信息是否为空
     *
     * @param ideaname
     * @param ideaintro
     * @param idrainfor
     * @param username
     * @param userphone
     * @return
     */
    private boolean check(String ideaname, String ideaintro, String idrainfor, String username, String userphone) {
        if (ideaname.isEmpty()) {
            showToastShort("请输入创意名称");
            return false;
        }
        if (ideaintro.isEmpty()) {
            showToastShort("请输入创意介绍");
            return false;
        }
        if (idrainfor.isEmpty()) {
            showToastShort("请输入创意详情");
            return false;
        }
        if (username.isEmpty()) {
            showToastShort("请输入姓名：");
            return false;
        }
        if (userphone.isEmpty()) {
            showToastShort("请输入电话号码");
            return false;
        }
        if (sTypeSpinner.isEmpty()) {
            showToastShort("请选择创意类型");
            return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                // 获取返回的图片列表
                path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                for (String p : path) {
                    System.out.println(p+"");
                    Glide.with(this).load(p).into(selectPic);
                }

            }
        }
    }

}
