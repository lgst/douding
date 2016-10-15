package com.ddgj.dd.util.net;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Administrator on 2016/10/12.
 */
public class HttpHelper implements NetWorkInterface {

    public static final void getAD(Callback callBack){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GET_AD).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });

    }

}
