package com.ddgj.dd.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.ddgj.dd.R;
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

/**
 * Created by Administrator on 2016/10/23.
 */

public class UpdateUtils implements NetWorkInterface {
    private static final String FLAG = "versions_android";
    public UpdateUtils(Context context){
        if (context == null) {
            throw new NullPointerException("context不能为空！");
        }
        mContext = context;
    }
    private Context mContext;

    public void checkVersion() {
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
                Log.i("lgst", "版本更新："+response);
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
        SweetAlertDialog ad = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        ad.setTitleText("更新提示")
                .setContentText("检测到新版本是否更新应用？")
                .setConfirmText("更新")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
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
        final SweetAlertDialog ad = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        final ProgressHelper ph = ad.getProgressHelper();
        ph.setCircleRadius(100);
        ph.setBarColor(mContext.getResources().getColor(R.color.colorPrimary));
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
        mContext.startActivity(intent);
    }

    /**
     * 获取版本号
     */
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        Log.i("lgst", "getVersionName: "+version);
        return version;
    }
}
