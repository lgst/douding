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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.FavoriteInfo;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.User;
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

import static com.ddgj.dd.util.net.NetWorkInterface.GET_PATENT_DETAIL;
import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

public class PatentDetailActivity extends BaseActivity {
    private ViewPager viewPager;
    private TextView mName, mPublishTime, mPublisher, mType, mIntro, mDesc, mCollect, mPageNumber;
    private Patent mPatent;
    private ImageView mChat;
    private boolean CHECKED;
    private ArrayList<View> viewList;
    private String[] imgs = {"1"};
    private MyAdapter myAdapter;
    private RelativeLayout mRelativeLayout;
    private ImageView favorite;
    private Toolbar toolbar;
    private String collection_id;

    @Override
    protected void initView() {
        mPageNumber = (TextView) findViewById(R.id.tv_page_number);
        mName = (TextView) findViewById(R.id.tv_name);
        mPublishTime = (TextView) findViewById(R.id.tv_publish_time);
        mPublisher = (TextView) findViewById(R.id.tv_publisher);
        mType = (TextView) findViewById(R.id.tv_type_text);
        mIntro = (TextView) findViewById(R.id.tv_intro_text);
        mDesc = (TextView) findViewById(R.id.tv_desc_text);
        mCollect = (TextView) findViewById(R.id.tv_collect_number);
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
                    startActivity(new Intent(PatentDetailActivity.this, LoginActivity.class));
            }
        });

        mChat = (ImageView) findViewById(R.id.iv_chat);

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getInstance().isLogined()) {
                    startActivity(new Intent(PatentDetailActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, mPatent.getAccount()));
                } else {
                    showToastShort("请登录！");
                    startActivity(new Intent(PatentDetailActivity.this, LoginActivity.class)
                            .putExtra("flag", LoginActivity.BACK));
                }
            }
        });

        mRelativeLayout = (RelativeLayout) findViewById(R.id.viewpager_pic);

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
                mPageNumber.setText(i + "/" + viewList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patent_detail);
        initData();
        initView();
        initToolBar();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.pop_menu_share);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("专利详情");
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
//                        Toast.makeText(PatentDetailActivity.this, "正在开发！",
//                                Toast.LENGTH_SHORT).show();
//                        break;
                }
                return true;
            }
        });
    }

    private void initData() {
        String stringExtra = getIntent().getStringExtra("patent_id");
        Log.e("patent", "2patent：" + stringExtra);
        Map<String, String> params = new HashMap<String, String>();
        params.put("patent_id", String.valueOf(stringExtra));
        OkHttpUtils.post().url(GET_PATENT_DETAIL).params(params).build().execute(new StringCallback() {
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

    private void JsonToDatas(String response) {

        JSONObject jo = null;
        try {
            jo = new JSONObject(response);
            int status = jo.getInt("status");
            if (status == STATUS_SUCCESS) {
                String string = jo.getString("data");
                Log.e("weiwei", "weiwei" + string);
                mPatent = new Gson().fromJson(string, Patent.class);
                User user = UserHelper.getInstance().getUser();
                if (user != null && !user.getAccount().equals(mPatent.getAccount())) {
                    mChat.setVisibility(View.VISIBLE);
                }
                if (mPatent != null) {
                    mName.setText(mPatent.getPatent_name());
                    mPublishTime.setText(mPatent.getP_creation_time());
                    mPublisher.setText(mPatent.getP_user_name());
                    mType.setText(mPatent.getPatent_type());
                    mIntro.setText(mPatent.getPatent_introduce());
                    mDesc.setText(mPatent.getPatent_details());
                    mCollect.setText(mPatent.getCollection_count());
                    //获取收藏状态
                    if (UserHelper.getInstance().isLogined()) {
                        DbUtils mDbu = DbUtils.create(getApplicationContext(), StringUtils.getDbName());
                        List<FavoriteInfo> fs = mDbu.findAll(Selector.from(FavoriteInfo.class)
                                .where("c_from_id", "=", mPatent.getPatent_id()));
                        if (fs != null && !fs.isEmpty()) {
                            //获取收藏状态
                            CHECKED = true;
                            favorite.setImageResource(R.mipmap.love_fill);
                            collection_id = fs.get(0).getCollection_id();
                        } else {
                            favorite.setImageResource(R.mipmap.love);
                        }
                    }
                    //得到轮播图片
                    // 将要分页显示的View装入数组中
                    viewList = new ArrayList<View>();
                    if (mPatent.getPatent_picture() != null) {
                        mRelativeLayout.setVisibility(View.VISIBLE);
                        imgs = mPatent.getPatent_picture().split("\\,");
                        for (int i = 0; i < imgs.length; i++) {
                            Log.e("toJson", "所有图片的链接" + imgs[i] + imgs.length);
                            // NetWorkInterface.HOST + "/" + imgs[i]
                            if (!imgs[i].equals("null")) {
                                ImageView imageView = new ImageView(PatentDetailActivity.this);
                                Glide.with(this).load(NetWorkInterface.HOST + "/" + imgs[i]).into(imageView);
                                viewList.add(imageView);
                            }
                        }
                        if (viewList.size() == 0) {
                            mPageNumber.setText("无");
                        } else {
                            mPageNumber.setText("1/" + viewList.size());
                        }
                        myAdapter.notifyDataSetChanged();
                    }

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
                .withTitle(mPatent.getPatent_name())
                .withText(mPatent.getPatent_details())
                .withTargetUrl(NetWorkInterface.HOST + mPatent.getShare_url())
                .withMedia(new UMImage(this, R.drawable.sina_web_default))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        Toast.makeText(PatentDetailActivity.this, "分享已完成！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        Toast.makeText(PatentDetailActivity.this, "分享出错！",
                                Toast.LENGTH_SHORT).show();

                        Log.e("lgst", arg1.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        Toast.makeText(PatentDetailActivity.this, "分享已取消！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).open();
    }

    /**
     * 收藏
     */
    private void favorite() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_type", "0");
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("c_u_account", mPatent.getAccount());
        params.put("c_from_id", mPatent.getPatent_id());
        params.put("c_from_title", mPatent.getPatent_name());
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
                        Snackbar.make(toolbar, "收藏成功！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(PatentDetailActivity.this, FavoriteActivity.class));
                            }
                        }).show();
                        EventBus.getDefault().post(new BusEvent(BusEvent.FAVORIT));
                    } else
                        Snackbar.make(toolbar, "您已经收藏过本条数据！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(PatentDetailActivity.this, FavoriteActivity.class));
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
                    .where("c_from_id", "=", mPatent.getPatent_id()));
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
                Log.e(TAG, "收藏删除失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "取消收藏成功: " + response);
                try {
                    mDbu.delete(FavoriteInfo.class, WhereBuilder.b("c_from_id", "=", finalC_from_id));
                } catch (DbException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(PatentDetailActivity.this, "取消成功！", Toast.LENGTH_SHORT).show();
            }
        });
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
