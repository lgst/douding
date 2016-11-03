package com.ddgj.dd.util.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ddgj.dd.activity.WebActivity;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * Created by lgst on 2016/11/03.<br>
 * 网络请求封装，根据请求地址获得对应的数据并封装成对应的集合，在回调方法中回传
 */
public class HttpHelper<T> implements NetWorkInterface {
    private Class<T> tClass;
    private Context mContext;
    private static final String TAG = "lgst";
    /**
     * 是否保存对应的JSON数据
     */
    private boolean save = false;

    /**
     * 上传用户头像
     */
    public static void uploadUserIcon(final Context context, final String path) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setConfirmText("重试")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                uploadUserIcon(context, path);
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
                if (((Activity)context).isFinishing())
                    sweetAlertDialog.show();
                Toast.makeText(context, "图像上传失败！", Toast.LENGTH_SHORT).show();
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
        final Request request = new Request.Builder()
                .url(UPDATE_USER_ICON)
                .post(requestBody)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("lgst", "用户头像上传失败：" + call.request().body().toString() + "\n-------" + e.getMessage());
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jo = new JSONObject(response.body().string());
                    UserHelper.getInstance().getUser().setHead_picture(jo.getString("data"));
                    UserHelper.getInstance().getUser().saveToSharedPreferences(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.i("lgst", "上传成功" + response.body().string());
            }
        });
    }

    /**
     * 创建一个HttpHelper实例<br>
     *
     * @param context :上下文环境<br>
     * @param tClass  :请求数据的实体类Class对象<br>
     * @param save    :是否保存JSON数据，缓存防止下次启动应用出现大片留白
     */
    public HttpHelper(Context context, Class<T> tClass, boolean save) {
        this.tClass = tClass;
        this.mContext = context;
        this.save = save;
    }

    /**
     * 创建一个HttpHelper实例<br>
     *
     * @param context :上下文环境<br>
     * @param tClass  :请求数据的实体类Class对象<br>
     */
    public HttpHelper(Context context, Class<T> tClass) {
        this.tClass = tClass;
        this.mContext = context;
    }

    /**
     * 获取数据， POST 请求<br>
     *
     * @param url      :接口链接<br>
     * @param params   :接口参数<br>
     * @param callback :回调<br>
     */
    public void getDatasPost(String url, Map<String, String> params, final DataCallback<T> callback) {
        OkHttpUtils.post().url(url).params(params).build().execute(
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showNetworkNotConnectToast();
//                        Log.e(TAG, "onError: " + e.getMessage());
                        callback.Failed(e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.i("lgst", response);
                        callback.Success(analysisAndLoadOriginality(response));
                        if (save)
                            FileUtil.saveJsonToCacha(response, tClass.getName());
                    }
                }
        );
    }

    /**
     * 获取数据，GET请求<br>
     *
     * @param url      :接口链接<br>
     * @param callback :回调<br>
     */
    public void getDatasGet(String url, final DataCallback<T> callback) {
        OkHttpUtils.get().url(url).build().execute(
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showNetworkNotConnectToast();
                        Log.e(TAG, "onError: " + e.getMessage());
                        callback.Failed(e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("lgst", response);
                        callback.Success(analysisAndLoadOriginality(response));
                    }
                }
        );
    }

    /**
     * 解析json数据<br>
     *
     * @param response :JSON数据
     */
    public List<T> analysisAndLoadOriginality(String response) {
        if (response == null) {
            return new ArrayList<T>();
        }
        List<T> datas = new ArrayList<T>();
        try {
            JSONObject jo = new JSONObject(response);
            int status = jo.getInt("status");
            if (status == STATUS_SUCCESS) {
                JSONArray ja = jo.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++) {
                    String patentStr = ja.getJSONObject(i).toString();
                    T data = (T) new Gson().fromJson(patentStr, tClass);
                    datas.add(data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return datas;
        }
    }

    /**
     * 跳转到详情页<br>
     *
     * @param url    :详情页链接地址获取接口
     * @param params :参数
     * @param data   :实体对象
     */
    public void startDetailsPage(String url, Map<String, String> params, final Object data) {
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("lgst", data.getClass().getName() + "获取详情页失败：" + e.getMessage());
                showNetworkNotConnectToast();
            }

            @Override
            public void onResponse(String response, int id) {
                ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                if (responseInfo.getStatus() == STATUS_SUCCESS) {
                    String url = responseInfo.getData();
                    Log.e("lgst", url);
                    startActivity(url, data);
                }
            }
        });
    }

    private void startActivity(String url, Object obj) {
        Intent intent = new Intent(mContext, WebActivity.class);
        if (obj instanceof Originality) {//创意详情
            Originality originality = (Originality) obj;
            intent.putExtra("title", originality.getOriginality_name())
                    .putExtra("url", HOST + url)
                    .putExtra("account", originality.getAccount())
                    .putExtra("content", originality.getOriginality_details())
                    .putExtra("id", originality.getOriginality_id())
                    .putExtra("classes", 0);
        } else if (obj instanceof Patent) {//专利详情
            Patent patent = (Patent) obj;
            intent.putExtra("title", patent.getPatent_name())
                    .putExtra("url", HOST + url)
                    .putExtra("account", patent.getAccount())
                    .putExtra("content", patent.getPatent_details());
        } else if (obj instanceof Order) {//订制详情
            Order order = (Order) obj;
            intent.putExtra("title", order.getMade_name())
                    .putExtra("url", HOST + url)
                    .putExtra("content", order.getMade_describe());
        }
        mContext.startActivity(intent);
    }

    private void showNetworkNotConnectToast() {
        Toast.makeText(mContext, "网络请求失败，请稍后重试！", Toast.LENGTH_SHORT).show();
    }


    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        if (mContext == null)
            throw new NullPointerException("参数不能为null！");
        this.mContext = mContext;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public Class<T> gettClass() {
        return tClass;
    }

    public void settClass(Class<T> tClass) {
        if (tClass == null)
            throw new NullPointerException("参数不能为null！");
        this.tClass = tClass;
    }
}
