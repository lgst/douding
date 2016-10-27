package com.ddgj.dd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.PostContentBean;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.RichTextEditor;
import com.ddgj.dd.view.RichTextEditor.EditData;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2016/10/24.
 */
public class PublishBBSActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE = 2;
    private List<String> path;
    private TextView commitBBS;
    private ImageView hideKeyboard;
    private ImageView selectPic;
    private EditText titleName;
    private RichTextEditor editor;
    private StringBuilder builder;
    private File file;
    private ArrayList<String> picList;
    private SweetAlertDialog dialog;
    private PostContentBean postContentBean;
    private PostContentBean postContentBean1;
    private ArrayList<PostContentBean> postContentList;
    private String toJson;

    @Override
    public void initView() {
        //富文本编辑器
        editor = (RichTextEditor) findViewById(R.id.richEditor);
        titleName = (EditText) findViewById(R.id.title_name);
        commitBBS = (TextView) findViewById(R.id.bbs_commit);
        commitBBS.setOnClickListener(this);
        hideKeyboard = (ImageView) findViewById(R.id.hide_keyboard);
        hideKeyboard.setOnClickListener(this);
        selectPic = (ImageView) findViewById(R.id.select_pic);
        selectPic.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_add);
        initView();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bbs_commit:
                List<EditData> editList = editor.buildEditData();
                // 下面的代码可以上传、或者保存，请自行实现
                dealEditData(editList);
                break;
            case R.id.hide_keyboard:
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.select_pic:
                MultiImageSelector.create(PublishBBSActivity.this)
                        .start(PublishBBSActivity.this, REQUEST_IMAGE);
                break;
            default:

                break;
        }
    }
    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    protected void dealEditData(List<EditData> editList) {
        String title = titleName.getText().toString().trim();
        builder = new StringBuilder();
        picList = new ArrayList<>();
        postContentList = new ArrayList<>();
        for (int i = 0; i < editList.size(); i++) {
            if ( editList.get(i).inputStr != null) {
                postContentBean = new PostContentBean(i, editList.get(i).inputStr);

                builder.append(editList.get(i).inputStr);
            } else if ( editList.get(i).imagePath!= null) {
                picList.add(editList.get(i).imagePath);
                postContentBean = new PostContentBean(i,"这里是图片");
            }
            postContentList.add(postContentBean);
        }

        Log.e("editList", "editListeditListeditList" + editList.size());

        //Java集合转换成Json集合
        toJson = new Gson().toJson(postContentList);
        Log.e("toJson", toJson);

        if (title.isEmpty()){
            showToastShort("请输入标题");
        }else if (editList.size()==1&&editList.get(0).inputStr.equals("")){
            showToastShort("请输入内容");
        }else {
                toCommitBBS(title);
        }


    }
    private void toCommitBBS(String title) {
        dialog = showLoadingDialog("", "正在发送");
        Map<String, String> params = new HashMap<String, String>();
        params.put("title",title);
        params.put("cordcontent", toJson);
        params.put("bbs_type", String.valueOf("1"));
        params.put("user_id", UserHelper.getInstance().getUser().getAccount_id());

        PostFormBuilder post = OkHttpUtils.post();

        if (picList!=null) {
            for (int i = 0; i < picList.size(); i++) {
                file = FileUtil.scal(Uri.parse(picList.get(i)), getCacheDir());
                String s = "picture";
                post.addFile(s + i, file.getName(), file);
                Log.e("fabubbs",picList.get(i));
            }
        }
        post.url(NetWorkInterface.PUBLISH_BBS)
                .params(params).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e("fabubbs", e.getMessage() + " 失败id:" + id);
                        showToastLong("失败");
                        dialog.dismiss();

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("fabubbs", " 成功id:" + id);
                        showToastLong("成功");
                        setResult(SUCCESS);
                        PublishBBSActivity.this.finish();
                        dialog.dismiss();


                    }
                });

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
                    insertBitmap(p);
                }

            }
        }
    }

    /**
     * 添加图片到富文本剪辑器
     *
     * @param imagePath
     */
    private void insertBitmap(String imagePath) {
        editor.insertImage(imagePath);
    }

    public void backClick(View v)
    {
        finish();
    }

}
