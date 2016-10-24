package com.ddgj.dd.util.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ddgj.dd.util.user.UserHelper;

import java.io.File;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/12.
 */
public class HttpHelper implements NetWorkInterface {
    public static void uploadUserIcon(final Context context, final String path) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setConfirmText("重试")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                uploadUserIcon(context,path);
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setTitleText("提示！")
                        .setContentText("图像上传失败！是否重试？")
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                sweetAlertDialog.show();
                Toast.makeText(context,"图像上传失败！",Toast.LENGTH_SHORT).show();
            }
        };
        File iconFile = new File(path);
        OkHttpClient client = new OkHttpClient();
        //创建RequestBody
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), iconFile);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("head_picture", "img.jpg", fileBody)
                .addFormDataPart("account_id", UserHelper.getInstance().getUser().getAccount_id())
                .build();
        Request request = new Request.Builder()
                .url(UPDATE_USER_ICON)
                .post(requestBody)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("lgst","用户头像上传失败："+call.request().body().toString()+"\n-------"+e.getMessage());
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.i("lgst","上传成功"+response.body().string());
            }
        });
    }
}
