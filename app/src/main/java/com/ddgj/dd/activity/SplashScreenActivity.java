package com.ddgj.dd.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ddgj.dd.R;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.ProgressHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Request;

import static com.ddgj.dd.util.net.NetWorkInterface.CHECK_UPDATE;


public class SplashScreenActivity extends BaseActivity {
    private ImageView imageView;
    private static final String FLAG = "versions_android";
    private boolean needUpdate = false;
    private boolean animFinished = false;
    private String url;

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView = (ImageView) findViewById(R.id.img);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_splash);
        imageView.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client_Type", FLAG);
                params.put("version_num", getVersionName());
                OkHttpUtils.post().url(CHECK_UPDATE).params(params).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        Log.e("lgst", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.i("lgst", response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            if (NetWorkInterface.STATUS_SUCCESS == status) {
                                url = "http://" + jo.getString("data");
                                if (jo.getString("sum").equals("0") && animFinished) {
                                    startMainActivity();
                                } else if (animFinished) {
                                    showUpdate(url);
                                }
                                needUpdate = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//        startActivity(new Intent(this,LoginActivity.class));
//        startActivity(new Intent(this,RegisterActivity.class));
                if (needUpdate) {
                    showUpdate(url);
                } else {
                    startMainActivity();
                }
                animFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }

    private void showUpdate(final String url) {
        SweetAlertDialog ad = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        ad.setTitleText("更新提示")
                .setContentText("检测到新版本是否更新应用？")
                .setConfirmText("更新")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startUpdate(url);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelText("退出")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        System.exit(0);
                    }
                })
                .setCancelable(false);
        ad.show();
    }

    private void startUpdate(String url) {
        final SweetAlertDialog ad = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        final ProgressHelper ph = ad.getProgressHelper();
        ph.setCircleRadius(100);
        ph.setBarColor(getResources().getColor(R.color.colorPrimary));
        ad.setCancelable(false);
        ad.setTitleText("正在下载更新，请稍等...").show();
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(FileUtil.getInstance().getmTempCache(), "ddgj_update.apk")//
                {
                    @Override
                    public void onBefore(Request request, int id) {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        ph.setInstantProgress(progress);
                        Log.e("lgst", "inProgress :" + progress);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lgst", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        Log.e("lgst", "onResponse :" + file.getAbsolutePath());
                        ad.dismiss();
                        installApk(file);
                    }
                });
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }


    /**
     * 获取版本号
     */
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }
}
