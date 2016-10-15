package com.ddgj.dd.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.ddgj.dd.R;

public class WebActivity extends BaseActivity {
    private RelativeLayout mContainer;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initViews();
        initWebView();
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient(){

        });
    }

    @Override
    public void initViews() {
        mContainer = (RelativeLayout) findViewById(R.id.content_container);
        mWebView = new WebView(getApplicationContext());
        mContainer.addView(mWebView);
    }
}
