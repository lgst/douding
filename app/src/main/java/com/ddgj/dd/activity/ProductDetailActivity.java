package com.ddgj.dd.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ddgj.dd.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_ORIGINALITY_DETAIL;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initData();
    }
    private void initData() {
        String stringExtra = getIntent().getStringExtra("originality_id");
        Log.e("originality_id", "originality_id：" + stringExtra);
        Map<String, String> params = new HashMap<String, String>();
        params.put("originality_id", String.valueOf(stringExtra));
        OkHttpUtils.post().url(GET_ORIGINALITY_DETAIL).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("weiwei", "weiwei" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("weiwei", "weiwei" + response);

            }
        });

    }
}
