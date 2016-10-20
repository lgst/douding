package com.ddgj.dd.util.camera;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * 从相册或相机获取图片并裁剪<br>
 * <p/>
 * 参数：<br>scaleType： 缩放比例 按照1：1比例缩放或自由裁剪 不传参数为默认值自由裁剪
 */
public class CameraActivity extends BaseActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CROP_REQUEST_CODE = 102;
    private static final int SUCCESS_RESULT_CODE = -1;
    /**
     * 输出路径
     */
    private Uri uri;
    private String tmpPath;
    private boolean isEqualsScale;
    private LinearLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        anim();
        init();
    }

    private void anim() {
        contentView = (LinearLayout) findViewById(R.id.act_camera_content);
        contentView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tran_in_from_bottom));
    }

    private void init() {
        if (getIntent().getBooleanExtra("scaleType", false)) {
            isEqualsScale = true;
        }
        String path = getIntent().getStringExtra("path");
        if (path == null) {
            throw new NullPointerException("没有传入图片保存路径！");
        }
        uri = Uri.fromFile(new File(path));
        tmpPath = FileUtil.getInstance().getmTempCache() + "tmp.jpg";
    }

    /**
     * 从相册选择图片点击事件
     *
     * @param v
     */
    public void galleryClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * 拍照点击事件
     *
     * @param v
     */
    public void graphClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tmpPath)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void cancelClick(View v) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.tran_out_from_bottom);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentView.startAnimation(anim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功返回-1，失败返回0
        if (resultCode == SUCCESS_RESULT_CODE) {
            //相册
            if (requestCode == GALLERY_REQUEST_CODE) {
                String path = data.getData().getPath();
                //部分rom返回的路径为 典型的file uri： content://media/external/images/media/74275
                //如果直接使用这个uri就拿不到图片文件，所以需要获取图片文件的真实路径
                if (path.contains("/external/images/media/")) {
                    //将uri转换成真正的图片路径
                    path = getRealPathFromUri(this, data.getData());
                }
                File sourceFile = new File(path);
                File targetFile = new File(tmpPath);
                copyFile(sourceFile, targetFile);
                cropPhoto();
            }
            //相机
            if (requestCode == CAMERA_REQUEST_CODE) {
                cropPhoto();
            }
            if (requestCode == CROP_REQUEST_CODE) {
                setResult(SUCCESS, new Intent().putExtra("path", uri.getPath()));
                clearTempFile();
                compressBitmap(uri.getPath(), (float) (1024 * 1024 * 8));// 1000:40
                finish();
            }
        }
    }

    private void cropPhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(tmpPath)), "image/*");//输入路径
        intent.putExtra("crop", "true");//裁剪
        if (isEqualsScale) {//1：1比例裁剪，不设置自由裁剪
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);//
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//输出路径
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出格式
        intent.putExtra("noFaceDetection", true);//人脸检测
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * 文件复制
     */
    private void copyFile(File source, File target) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            int len = 0;
            byte[] buf = new byte[1024 * 1024 * 3];//缓冲区 3MB
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    compress

    /**
     * 删除临时文件
     */
    private boolean clearTempFile() {
        File tempFile = new File(tmpPath);
        if (tempFile.exists() && tempFile.isFile()) {
            return tempFile.delete();
        } else {
            Log.i("lgst", "没有临时文件！");
            return false;
        }
    }

    /**
     * 将uri转换为真实的路径
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 图片压缩
     * path：路径
     * maxSize：最大值 KB
     */
    private void compressBitmap(String path, float maxSize) {
        Bitmap bitmap = decodeFile(path);
        if (bitmap == null) {
            return;
        }
        if (bitmap.getByteCount() <= maxSize)
            return;
        Matrix matrix = new Matrix();
        //计算缩放比例
        float scale = maxSize / (float) bitmap.getByteCount();
        //设置矩阵缩放比例
        matrix.setScale(scale, scale);
        //根据矩阵创建Bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        //保存图片
        OutputStream outst = null;
        try {
            outst = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (outst != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outst);
    }

    @Override
    public void initView() {

    }
}
