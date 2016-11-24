package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.FavoriteInfo;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.PostBean;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_ORIGINALITY_DETAIL;
import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

/**
 * Created by Administrator on 2016/11/21.
 */
public class OriginalityDetailActivity extends BaseActivity {

    private  boolean CHECKED ;
    private TextView textView, textView1, textView2, textView3, textView4, textView5,
            textView6,textView7,textView8,textView9,textView10;
    private Toolbar toolbar;
    private Originality originality;
    private ImageView imageView;
    private ImageView imageView1;
    private ImageView imageView2;
    private ViewPager viewPager;
    private MyAdapter myAdapter;
    private String[] imgs={"1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_originality_detail);
        initData();
        initToolBar();
        initView();
    }

    private void initData() {
        String stringExtra = getIntent().getStringExtra("originality_id");
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
                JsonToDatas(response);
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.pop_menu_share);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        share();
                        break;
                    case R.id.update:
                        Toast.makeText(OriginalityDetailActivity.this, "正在开发！",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    @Override
    protected void initView() {
        textView = (TextView) findViewById(R.id.tv_originality_name);
        textView1 = (TextView) findViewById(R.id.tv_publish_time);
        textView2 = (TextView) findViewById(R.id.tv_publisher);
        textView3 = (TextView) findViewById(R.id.tv_originality_type_text);
        textView4 = (TextView) findViewById(R.id.tv_originality_intro_text);
        textView5 = (TextView) findViewById(R.id.tv_originality_desc_text);
        textView6 = (TextView) findViewById(R.id.tv_page_number);
        textView7 = (TextView) findViewById(R.id.tv_collect);
        textView8 = (TextView) findViewById(R.id.tv_collect_number);
        textView9 = (TextView) findViewById(R.id.tv_comment);
        textView10 = (TextView) findViewById(R.id.tv_comment_number);

        viewPager = (ViewPager) findViewById(R.id.vp_image);
        myAdapter = new MyAdapter();
        viewPager.setAdapter(myAdapter);


        imageView = (ImageView) findViewById(R.id.iv_chat);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OriginalityDetailActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, originality.getAccount()));
            }
        });
        imageView1 = (ImageView) findViewById(R.id.iv_love);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CHECKED){
                    // deleteData();
                    imageView1.setImageResource(R.mipmap.love);
                    CHECKED=false;
                }else {
                    // favorite();
                    imageView1.setImageResource(R.mipmap.love_fill);
                    CHECKED=true;

                }

            }
        });
        imageView2 = (ImageView) findViewById(R.id.iv_comment);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = UserHelper.getInstance().getUser();
                if (user == null) {
                    showToastShort("请登录后，再评论！");
                    startActivity(new Intent(OriginalityDetailActivity.this, LoginActivity.class)
                            .putExtra("flag", LoginActivity.BACK));
                    return;
                }
                startActivity(new Intent(OriginalityDetailActivity.this, CommentListActivity.class)
                        .putExtra("topic_id", originality.getOriginality_id())
                        .putExtra("classes", "0"));
            }
        });
    }
    /**
     * 收藏
     */
    private void favorite() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_type", String.valueOf(getIntent().getIntExtra("classes", -1)));
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("c_u_account", originality.getAccount());
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
                        Snackbar.make(textView,"收藏成功！",Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(OriginalityDetailActivity.this,FavoriteActivity.class));
                            }
                        }).show();
                    else
                        Snackbar.make(textView,"您已经收藏过本条数据！",Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(OriginalityDetailActivity.this,FavoriteActivity.class));
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
     * 取消收藏
     */
    private void deleteData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_id", originality.getOriginality_id());
        OkHttpUtils.post().params(params).url(NetWorkInterface.DELETE_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "收藏删除失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "删除收藏成功: " + response);

                Toast.makeText(OriginalityDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void JsonToDatas(String response) {

        JSONObject jo = null;
        try {
            jo = new JSONObject(response);
            int status = jo.getInt("status");
            if (status == STATUS_SUCCESS) {
                String string = jo.getString("data");
                Log.e("weiwei", "weiwei" + string);
                originality = new Gson().fromJson(string, Originality.class);
                User user = UserHelper.getInstance().getUser();
                if (user != null) {
                    if (!user.getAccount().equals(originality.getAccount())) {
                        imageView.setVisibility(View.VISIBLE);
                    }else {
                       // deletePost.setVisibility(View.VISIBLE);
                    }
                }
                if (originality != null) {
                    textView.setText(originality.getOriginality_name());
                    textView1.setText(originality.getO_creation_time());
                    textView2.setText(originality.getO_user_name());
                    textView3.setText(originality.getOriginality_type());
                    textView4.setText(originality.getOriginality_introduce());
                    textView5.setText(originality.getOriginality_details());
                    // textView6.setText(originality.getOriginality_name());
                    textView8.setText(originality.getCollection_count());
                    textView10.setText(originality.getComments_count());
                    //获取收藏状态
                    CHECKED=false;
                    //得到轮播图片
                    imgs = originality.getO_picture().split("\\,");
                    for (int i = 0; i < imgs.length; i++) {
                        Log.e("toJson", "所有图片的链接" + imgs[i]);
                        // NetWorkInterface.HOST + "/" + imgs[i]
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 分享
     */
    private void share() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE};
        new ShareAction(this)
                .setDisplayList(displaylist)
                .withTitle(getIntent().getStringExtra("title"))
                //.withText(mContentText)
                // .withTargetUrl(mUrl)
                .withMedia(new UMImage(this, R.drawable.sina_web_default))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        Toast.makeText(OriginalityDetailActivity.this, "分享已完成！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        Toast.makeText(OriginalityDetailActivity.this, "分享出错！",
                                Toast.LENGTH_SHORT).show();
                        arg1.printStackTrace();
                        Log.e("lgst", arg1.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        Toast.makeText(OriginalityDetailActivity.this, "分享已取消！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).open();
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {

            return imgs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }
}
