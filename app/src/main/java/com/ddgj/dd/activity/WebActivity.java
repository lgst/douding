package com.ddgj.dd.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddgj.dd.R;

public class WebActivity extends BaseActivity {
    private RelativeLayout mContainer;
    private WebView mWebView;
    private android.widget.TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        initWebView();
    }

    private void initWebView() {

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mWebView.loadUrl("file:///android_asset/error.html");
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(false);
        String url = getIntent().getStringExtra("url");
        if (!checkNetWork()) {
            showToastNotNetWork();
            mWebView.loadUrl("file:///android_asset/error.html");
            return;
        }
        mWebView.loadUrl(url);
    }

    @Override
    public void initView() {
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(getIntent().getStringExtra("title"));
        mContainer = (RelativeLayout) findViewById(R.id.content_container);
        mWebView = new WebView(getApplicationContext());
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mWebView, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearCache(true);
        mWebView.destroy();
    }

    public void backClick(View v) {
        finish();
    }
}
