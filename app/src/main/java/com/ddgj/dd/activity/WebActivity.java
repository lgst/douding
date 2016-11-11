package com.ddgj.dd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.PinchImageView;
import com.hyphenate.easeui.EaseConstant;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.R.id.favorite;

public class WebActivity extends BaseActivity implements Animation.AnimationListener {
    private RelativeLayout mContainer;
    private WebView mWebView;
    private TextView mTitle;
    private String mAccount;
    private String mUrl;
    private String mContentText;
    private PinchImageView mPiv;
    private LinearLayout commentContainerLL;
    private InputMethodManager imm;
    private boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mAccount = getIntent().getStringExtra("account");
        initView();
        initWebView();
    }

    /**
     * 初始化WebView
     */
    private void initWebView() {

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                addImageClickListner();
                imgReset();
                Log.i("lgst", "finish");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:") || url.startsWith("mailto:")) {
                    return true;
                }
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
        mUrl = getIntent().getStringExtra("url");
        if (!checkNetWork()) {
            showToastNotNetWork();
            mWebView.loadUrl("file:///android_asset/error.html");
            return;
        }
        JavaScriptInterface javascriptInterface = new JavaScriptInterface();
        mWebView.addJavascriptInterface(javascriptInterface, "imagelistner");
        mWebView.loadUrl(mUrl);
        Log.i(TAG, "initWebView: " + mUrl);
    }

    @Override
    public void initView() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        标题
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(getIntent().getStringExtra("title"));
        commentContainerLL = (LinearLayout) findViewById(R.id.comment_container);
//        if (getIntent().getIntExtra("classes", -1) != -1) {
        if (getIntent().getIntExtra("classes", -1) == 1)
            commentContainerLL.setVisibility(View.VISIBLE);
//        }
        mContainer = (RelativeLayout) findViewById(R.id.content_container);
        mWebView = new WebView(getApplicationContext());
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mWebView, params);
//        大图
        mPiv = (PinchImageView) findViewById(R.id.big_img);
        mPiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Animation animation = AnimationUtils.loadAnimation(WebActivity.this, R.anim.alpha_out);
                v.clearAnimation();
                v.setAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mPiv.setImageResource(R.drawable.shape_alpha_img);
                        v.setVisibility(View.GONE);
                        if (getIntent().getIntExtra("classes", -1) == 0)
                            commentContainerLL.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearCache(true);
        mWebView.destroy();
    }

    /**
     * 返回
     * @param v
     */
    public void backClick(View v) {
        finish();
    }

    /**
     * 更多
     * @param v
     */
    public void moreClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        String user = null;
        if (UserHelper.getInstance().getUser() != null) {
            user = UserHelper.getInstance().getUser().getAccount();
        }
        if (user == null) {//未登录
            popup.getMenuInflater().inflate(R.menu.pop_menu_mine, popup.getMenu());
        } else if (mAccount == null) {//没有发布人，广告没有发布人
            popup.getMenuInflater().inflate(R.menu.pop_menu_mine, popup.getMenu());
        } else if (mAccount.equals(user)) {//发布人是自己
            popup.getMenuInflater().inflate(R.menu.pop_menu_mine, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.pop_menu, popup.getMenu());
        }
//        通过反射开启图标显示
        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popup);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.send_message://发送消息
                        startActivity(new Intent(WebActivity.this, ChatActivity.class)
                                .putExtra(EaseConstant.EXTRA_USER_ID, mAccount));
                        break;
                    case R.id.share://分享
                        share();
                        break;
                    case favorite://收藏
                        favorite();
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    /**
     * 收藏
     */
    private void favorite() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_type", String.valueOf(getIntent().getIntExtra("classes", -1)));
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("c_u_account", mAccount);
        params.put("c_from_id", getIntent().getStringExtra("id"));
        params.put("c_from_title", getIntent().getStringExtra("title"));
        params.put("c_from_picture", "");
        OkHttpUtils.post().params(params).url(NetWorkInterface.ADD_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "收藏失败: " + e.getMessage());
                showToastShort("网络请求失败，请稍后重试！");
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (0 == jo.getInt("status"))
                        Snackbar.make(mTitle,"收藏成功！",Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(WebActivity.this,FavoriteActivity.class));
                            }
                        }).show();
                    else
                        Snackbar.make(mTitle,"您已经收藏过本条数据！",Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(WebActivity.this,FavoriteActivity.class));
                            }
                        }).show();
//                        showToastShort("收藏成功！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 分享
     */
    private void share() {
        mContentText = getIntent().getStringExtra("content");
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE};
        new ShareAction(this)
                .setDisplayList(displaylist)
                .withTitle(getIntent().getStringExtra("title"))
                .withText(mContentText)
                .withTargetUrl(mUrl)
                .withMedia(new UMImage(this, R.drawable.sina_web_default))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        Toast.makeText(WebActivity.this, "分享已完成！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        Toast.makeText(WebActivity.this, "分享出错！",
                                Toast.LENGTH_SHORT).show();
                        arg1.printStackTrace();
                        Log.e("lgst", arg1.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        Toast.makeText(WebActivity.this, "分享已取消！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).open();
    }

    /**
     * 评论
     * @param v
     */
    public void commentClick(View v) {
        User user = UserHelper.getInstance().getUser();
        if (user == null) {
            showToastShort("请登录后，再评论！");
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("flag", LoginActivity.BACK));
            return;
        }
        startActivity(new Intent(this, CommentListActivity.class)
                .putExtra("topic_id", getIntent().getStringExtra("id"))
                .putExtra("classes", getIntent().getIntExtra("classes", -1)));
    }

    /**
     * 点赞
     * @param v
     */
    public void supportClick(View v) {
        OkHttpUtils.get().url(NetWorkInterface.ORIGINALITY_SUPPORT + "?originality_id=" + getIntent().getStringExtra("id"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: 创意点赞失败" + e.getMessage());
                showToastNotNetWork();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "创意点赞结果：onResponse: " + response);
                JSONObject jo = null;
                try {
                    jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        showToastShort("点赞成功！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void imgReset() {
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%';   " +
                "}" +
                "})()");
    }

    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String img = (String) msg.obj;
            mPiv.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(WebActivity.this, R.anim.alpha);
            Glide.with(WebActivity.this).load(img).animate(R.anim.alpha).into(mPiv);
            mPiv.setAnimation(animation);
        }
    };

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        commentContainerLL.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    class JavaScriptInterface {

        public JavaScriptInterface() {
        }

        //点击图片回调方法
        //必须添加注解,否则无法响应
        @JavascriptInterface
        public void openImage(String img) {
            Log.i("lgst", "响应点击事件!");
            Message msg = new Message();
            msg.obj = img;
            handler.sendMessage(msg);
        }
    }
}
