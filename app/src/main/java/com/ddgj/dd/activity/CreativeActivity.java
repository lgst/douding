package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.camera.SelectPicPopupWindow;
import com.soundcloud.android.crop.Crop;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/13.
 */

public class CreativeActivity extends BaseActivity implements View.OnClickListener {

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

    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
    private static final int REQUESTCODE_TAKE = 10;    // 相机拍照标记
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private ImageView selectPic;
    private InputStream inputStream;

    @Override
    public void initView() {
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
        setContentView(R.layout.activity_creative);
        initView();
        initModeSpinner();
    }

    private void initModeSpinner() {
        String[] mItems = getResources().getStringArray(R.array.secrecyType);
        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,R.layout.textview_spinner_item,mItems);
        modeSpinner.setAdapter(spinnerAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sModeSpinner = parent.getItemAtPosition(position).toString();
               // Toast.makeText(CreativeActivity.this, "你点击的是:"+sModeSpinner, Toast.LENGTH_LONG).show();
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
                CreativeActivity.this.finish();
                break;
            case R.id.pick_pic:
               // this.startActivity(new Intent(this, CameraActivity.class).putExtra("pickPic",1));
                menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.root_view),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径 Uri.fromFile(new File(getCacheDir(), "cropped")
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    selectPic.setImageDrawable(null);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                /*    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);*/
                    selectPic.setImageDrawable(null);
                    Crop.pickImage(CreativeActivity.this);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else if (requestCode == REQUESTCODE_TAKE) {
            // 调用相机拍照
            File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
            beginCropTake(Uri.fromFile(temp));
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.e("error", orientation + "kanmkankankankna");
            Log.e("error", orientation + "kanmkankankankna");
            Log.e("error", orientation + "kanmkankankankna");
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void beginCropTake(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            Log.e("error", "URL" + Crop.getOutput(result));
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = 8;
            Bitmap cameraBitmap = BitmapFactory.decodeFile(Crop.getOutput(result).getPath(), bitmapOptions);
            int bitmapDegree = getBitmapDegree(Crop.getOutput(result).getPath());
            Bitmap bitmap = rotateBitmapByDegree(cameraBitmap, bitmapDegree);
            //将位图保存到本地
            saveBitmapToImag(bitmap);
            //将Bitmap图转换为本地文件，并返回路径
            String saveFileUrl = FileUtil.saveFile(this, "temphead.jpg", bitmap);
            //把头像文件保存到服务器
            Log.e("error", "saveFileUrl需要保存的路径是：" + saveFileUrl);
            //saveUserInfoPhoto(saveFileUrl);
            selectPic.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmapToImag(Bitmap bitmap) {
        FileOutputStream mFileOutputStream = null;

        try {
            File mFile = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.ddgj/tempPhoto.png");
            //创建文件
            mFile.createNewFile();
            //创建文件输出流
            mFileOutputStream = new FileOutputStream(mFile);
            //保存Bitmap到PNG文件
            //图片压缩质量为75，对于PNG来说这个参数会被忽略
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, mFileOutputStream);
            //Flushes this stream.
            //Implementations of this method should ensure that any buffered data is written out.
            //This implementation does nothing.
            mFileOutputStream.flush();
        } catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                //  handle exception
                e.printStackTrace();
            }
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
            File file = new File("file:///android_asset/ic_launcher.png");
            file.getAbsoluteFile();
            System.out.println("1文件路径"+file.getAbsoluteFile());

            OkHttpUtils.post()
                    .addFile("o_picture","ic_launcher.png",new File("file:///android_asset/ic_launcher.png"))
//                    .url(NetWorkInterface.ADD_IDEA)
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
        Log.e("douding","shuju"+sEditName+sEditIntro+sEditInfor+sEditUserName+sEditUserEmail+sEditUserPhone+sTypeSpinner+sModeSpinner);


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
            showToastShort("请输入姓名");
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

}
