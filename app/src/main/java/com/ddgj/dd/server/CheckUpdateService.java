package com.ddgj.dd.server;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

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

public class CheckUpdateService extends Service implements NetWorkInterface {
    private static final String FLAG = "versions_android";

    public CheckUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, String> params = new HashMap<String, String>();
        params.put("Client_Type", FLAG);
        params.put("version_num", getVersionName());
        OkHttpUtils.post().url(CHECK_UPDATE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("lgst", response);
                try {
                    JSONObject jo = new JSONObject(response);
                    int status = jo.getInt("status");
                    if (STATUS_SUCCESS == status) {
                        String url = "http://" + jo.getString("data");
                        showUpdate(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showUpdate(final String url) {
        SweetAlertDialog ad = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.NORMAL_TYPE);
        ad.setTitleText("更新提示")
                .setContentText("检测到新版本是否更新应用？")
                .setConfirmText("更新")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startUpdate(url);
                    }
                })
                .setCancelText("暂不更新")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelable(true);
        ad.show();
    }

    private void startUpdate(String url) {
        final SweetAlertDialog ad = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);
        final ProgressHelper ph = ad.getProgressHelper();
        ad.setTitleText("正在下载更新，请稍等...").show();
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(FileUtil.getInstance().getmTempCache(), "ddgj_update.apk")//
                {
                    @Override
                    public void onBefore(Request request, int id) {
                    }
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        ph.setProgress((int) (100 * progress));
                        Log.e("lgst", "inProgress :" + (int) (100 * progress));
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lgst", "onError :" + e.getMessage());
                    }
                    @Override
                    public void onResponse(File file, int id) {
                        Log.e("lgst", "onResponse :" + file.getAbsolutePath());
                        ad.dismiss();
                    }
                });
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
