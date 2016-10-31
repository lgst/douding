package com.ddgj.dd.activity;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.TextCheck;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2016/10/13.
 */

public class PublishProductActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

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
    private LinearLayout addImageGroup;
    private PostFormBuilder o_picture2;
    private SweetAlertDialog dialog;
    private String nickname;
    private String facilitator_name;
    private String account_id;
    private String head_picture;


    @Override
    public void initView() {

        backUp = (ImageView) findViewById(R.id.backup);
        backUp.setOnClickListener(this);
        editName = (EditText) findViewById(R.id.edit_name);
        editIntro = (EditText) findViewById(R.id.edit_intro);
        editInfor = (EditText) findViewById(R.id.edit_infor);
        userNmae = (EditText) findViewById(R.id.idea_user_name);

        userEmail = (EditText) findViewById(R.id.idea_user_email);
        userEmail.setOnFocusChangeListener(this);
        userPhone = (EditText) findViewById(R.id.idea_user_phone);
        userPhone.setOnFocusChangeListener(this);
        pickPic = (Button) findViewById(R.id.pick_pic);
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        pickPic.setOnClickListener(this);
        modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        commitIdea = (Button) findViewById(R.id.commit_idea);
        commitIdea.setOnClickListener(this);
        selectPic = (ImageView) findViewById(R.id.select_pic);

        //添加图片
        addImageGroup = (LinearLayout) findViewById(R.id.all_pic);
    }

    /**
     * 获取用户信息
     */
    private void initUser() {
        account_id = UserHelper.getInstance().getUser().getAccount_id();
        head_picture = UserHelper.getInstance().getUser().getHead_picture();
        if(UserHelper.getInstance().getUser() instanceof PersonalUser){
            nickname = ((PersonalUser) UserHelper.getInstance().getUser()).getNickname();
        }if(UserHelper.getInstance().getUser() instanceof EnterpriseUser){
            facilitator_name = ((EnterpriseUser) UserHelper.getInstance().getUser()).getFacilitator_name();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);
        initUser();
        initView();
        initModeSpinner();
    }

    private void initModeSpinner() {

        String[] mItems1 = getResources().getStringArray(R.array.originalityTypes);
        ArrayAdapter spinnerAdapter1=new ArrayAdapter(this,R.layout.textview_spinner_item,mItems1);
        typeSpinner.setAdapter(spinnerAdapter1);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sTypeSpinner = String.valueOf(position);

                //Toast.makeText(PublishCreativeActivity.this, "你点击的是:"+position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
//                PublishCreativeActivity.this.finish();
                showDailog();
                break;
            case R.id.pick_pic:
                //this.startActivity(new Intent(this, CameraActivity.class).putExtra("pickPic",1));
                MultiImageSelector.create(PublishProductActivity.this)
                        .start(PublishProductActivity.this, REQUEST_IMAGE);
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
            dialog = showLoadingDialog("", "正在发送您的产品");
            Map<String, String> params = new HashMap<String, String>();
            params.put("originality_name", String.valueOf(sEditName));
            params.put("originality_introduce", String.valueOf(sEditIntro));
            params.put("originality_details", String.valueOf(sEditInfor));
            params.put("originality_type", String.valueOf(sTypeSpinner));
            params.put("o_user_name", String.valueOf(sEditUserName));
            params.put("o_user_contact", String.valueOf(sEditUserPhone));
            params.put("o_user_email", String.valueOf(sEditUserEmail));
            params.put("o_secrecy_type", String.valueOf(sModeSpinner));
//            params.put("o_originality_address", "o_originality_address");
            params.put("o_account_id", account_id);
//            params.put("o_nickname", "o_nickname");
//            params.put("head_picture", "head_picture");
            params.put("originality_differentiate", "1");


            //file = new File(path.get(0));
            //file = FileUtil.scal(Uri.parse(path.get(0)));
            //file1 = FileUtil.scal(Uri.parse(path.get(1)));
            File cacheDir = getCacheDir();

            PostFormBuilder post = OkHttpUtils.post();
            if (path!=null) {
                for (int i = 0; i < path.size(); i++) {
                    file = FileUtil.scal(Uri.parse(path.get(i)), cacheDir);
                    String s = "o_picture";
                    post.addFile(s + i, file.getName(), file);

                }
            }
            post.url(NetWorkInterface.ADD_IDEA)
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
                            PublishProductActivity.this.finish();
                            dialog.dismiss();
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

        int id = this.typeSpinner.getSelectedItemPosition();

        //Log.e("douding","shuju模式id"+id+sEditName+sEditIntro+sEditInfor+sEditUserName+sEditUserEmail+sEditUserPhone+sTypeSpinner+sModeSpinner);


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
                    int px = DensityUtil.dip2px(this, 60);
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

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.idea_user_email:
                if (b) {
                    // 此处为得到焦点时的处理内容
                    //showToastShort("此处为得到焦点时的处理内容");
                } else {
                    // 此处为失去焦点时的处理内容
                    sEditUserEmail = this.userEmail.getText().toString().trim();

                    if (!TextCheck.checkEmail(sEditUserEmail)){
                        showToastShort("邮箱格式不正确");
                    }
                }
                break;
            case R.id.idea_user_phone:
                if (b) {
                } else {
                    sEditUserPhone = this.userPhone.getText().toString().trim();
                    if (!TextCheck.checkPhoneNumber(sEditUserPhone)){
                        showToastShort("手机号码格式不正确");
                    }
                }
            default:
                break;
        }
    }

    static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
//            Manifest.permission.READ_PHONE_STATE,        //读取设备信息
//            Manifest.permission.ACCESS_COARSE_LOCATION, //百度定位
//            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void process(Bundle savedInstanceState) {
        super.process(savedInstanceState);

        //如果有什么需要初始化的，在这里写就好～

    }

    @Override
    public void getAllGrantedPermission() {
        //当获取到所需权限后，进行相关业务操作

        super.getAllGrantedPermission();
    }

    @Override
    public String[] getPermissions() {
        return PERMISSION;
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