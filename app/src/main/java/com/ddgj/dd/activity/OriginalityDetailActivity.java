package com.ddgj.dd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.FavoriteInfo;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.bean.User;
import com.ddgj.dd.util.L;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.BusEvent;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_ORIGINALITY_DETAIL;
import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

/**
 * Created by Administrator on 2016/11/21.
 */
public class OriginalityDetailActivity extends BaseActivity {

    private boolean CHECKED;
    private TextView textView, textView1, textView2, textView3, textView4, textView5,
            textView6, textView7, textView8, textView9, textView10;
    private Toolbar toolbar;
    private Originality originality;
    private ImageView sendMessage;
    private ImageView favorite;
    private ImageView comment;
    private ViewPager viewPager;
    private MyAdapter myAdapter;
    private String[] imgs = {"1"};
    private ArrayList<View> viewList;
    private String shareUrl;
    private ArrayList<String> mImagesList;
    private String collection_id;

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
                getShareH5();
            }
        });

    }

    /**
     * 获取h5的分享界面
     */
    private void getShareH5() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_side", "app");
        params.put("originality_id", originality.getOriginality_id());
        OkHttpUtils.post().url(NetWorkInterface.HOST + "/originalityDet.do").params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                if (responseInfo.getStatus() == STATUS_SUCCESS) {
                    shareUrl = responseInfo.getData();
                }
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.pop_menu_share);
        toolbar.setTitle("创意详情");
        toolbar.setTitleTextColor(Color.WHITE);
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
//                    case R.id.update:
//                        Toast.makeText(OriginalityDetailActivity.this, "正在开发！",
//                                Toast.LENGTH_SHORT).show();
//                        break;
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i = position + 1;
                textView6.setText(i + "/" + viewList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        sendMessage = (ImageView) findViewById(R.id.iv_chat);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getInstance().isLogined())
                    startActivity(new Intent(OriginalityDetailActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, originality.getAccount()));
                else startActivity(new Intent(OriginalityDetailActivity.this, LoginActivity.class));
            }
        });
        favorite = (ImageView) findViewById(R.id.iv_love);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getInstance().isLogined())
                    if (CHECKED) {
                        deleteData();
                        favorite.setImageResource(R.mipmap.love);
                        CHECKED = false;
                    } else {
                        favorite();
                        favorite.setImageResource(R.mipmap.love_fill);
                        CHECKED = true;
                    }
                else
                    startActivity(new Intent(OriginalityDetailActivity.this, LoginActivity.class));
            }
        });
        comment = (ImageView) findViewById(R.id.iv_comment);
        comment.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (UserHelper.getInstance().isLogined() &&
                originality != null &&
                !UserHelper.getInstance().getUser().getAccount_id().equals(originality.getO_account_id()))
            sendMessage.setVisibility(View.VISIBLE);
        else {
            sendMessage.setVisibility(View.GONE);
        }
    }

    /**
     * 收藏
     */
    private void favorite() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_type", "1");
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("c_u_account", originality.getAccount());
        params.put("c_from_id", originality.getOriginality_id());
        params.put("c_from_title", originality.getOriginality_name());
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
                    if (0 == jo.getInt("status")) {
                        Snackbar.make(textView, "收藏成功！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(OriginalityDetailActivity.this, FavoriteActivity.class));
                            }
                        }).show();
                        EventBus.getDefault().post(new BusEvent(BusEvent.FAVORIT));
                    } else
                        Snackbar.make(textView, "您已经收藏过本条数据！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(OriginalityDetailActivity.this, FavoriteActivity.class));
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
        String c_from_id = null;
        final DbUtils mDbu = DbUtils.create(getApplicationContext(), StringUtils.getDbName());
        try {
            FavoriteInfo info = mDbu.findFirst(Selector.from(FavoriteInfo.class)
                    .where("c_from_id", "=", originality.getOriginality_id()));
            if (info == null) {
                return;
            }
            c_from_id = info.getC_from_id();
            collection_id = info.getCollection_id();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (c_from_id == null) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_id", collection_id);
        final String finalC_from_id = c_from_id;
        OkHttpUtils.post().params(params).url(NetWorkInterface.DELETE_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e(TAG, "收藏删除失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    mDbu.delete(FavoriteInfo.class, WhereBuilder.b("c_from_id", "=", finalC_from_id));
//                    mDbu.close();
                    L.i("删除收藏成功: " + response);
                } catch (DbException e) {
                    e.printStackTrace();
                }
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
                if (UserHelper.getInstance().isLogined()) {
                    DbUtils mDbu = DbUtils.create(getApplicationContext(), StringUtils.getDbName());
                    List<FavoriteInfo> fs = mDbu.findAll(Selector.from(FavoriteInfo.class)
                            .where("c_from_id", "=", originality.getOriginality_id()));
                    if (fs != null && !fs.isEmpty()) {
                        //获取收藏状态
                        CHECKED = true;
                        favorite.setImageResource(R.mipmap.love_fill);
                        collection_id = fs.get(0).getCollection_id();
                    } else {
                        favorite.setImageResource(R.mipmap.love);
                    }
                }
                if (originality != null) {
                    textView.setText(originality.getOriginality_name());
                    textView1.setText(originality.getO_creation_time());
                    textView2.setText(originality.getO_user_name());
                    textView3.setText(originality.getOriginality_type());
                    textView4.setText(originality.getOriginality_introduce());
                    textView5.setText(originality.getOriginality_details());

                    textView8.setText(originality.getCollection_count());
                    textView10.setText(originality.getComments_count());
                    if (UserHelper.getInstance().isLogined() &&
                            !UserHelper.getInstance().getUser().getAccount_id().equals(originality.getO_account_id()))
                        sendMessage.setVisibility(View.VISIBLE);
                    else {
                        sendMessage.setVisibility(View.GONE);
                    }
                    //得到轮播图片
                    // 将要分页显示的View装入数组中
                    viewList = new ArrayList<View>();
                    mImagesList = new ArrayList<String>();
                    if (originality.getO_picture() == null)
                        imgs = new String[0];
                    else
                        imgs = originality.getO_picture().split("\\,");
                    for (int i = 0; i < imgs.length; i++) {
                        Log.e("toJson", "所有图片的链接" + imgs[i] + imgs.length);
                        // NetWorkInterface.HOST + "/" + imgs[i]
                        if (!imgs[i].equals("null")) {
                            ImageView imageView = new ImageView(OriginalityDetailActivity.this);
                            Glide.with(this).load(NetWorkInterface.HOST + "/" + imgs[i]).into(imageView);
                            mImagesList.add(NetWorkInterface.HOST + "/" + imgs[i]);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(OriginalityDetailActivity.this, PreviewImageActivity.class)
                                            .putExtra(PreviewImageActivity.PARAMAS_POSITION, viewPager.getCurrentItem())
                                            .putStringArrayListExtra(PreviewImageActivity.PARAMAS_IMAGES, mImagesList));
                                }
                            });
                            viewList.add(imageView);
                        }
                    }
                    if (viewList.size() == 0) {
                        textView6.setText("无");
                        viewPager.setVisibility(View.GONE);//隐藏
                    } else {
                        textView6.setText("1/" + viewList.size());
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
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
                .withTitle(originality.getOriginality_name())
                .withText(originality.getOriginality_details())
                .withTargetUrl(NetWorkInterface.HOST + shareUrl)
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
            if (viewList != null) {
                return viewList.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    }
}
