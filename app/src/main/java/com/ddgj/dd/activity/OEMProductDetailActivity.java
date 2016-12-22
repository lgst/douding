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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.OEMProduct;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_DAIGONF_PRODUCT_DETAIL;
import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

public class OEMProductDetailActivity extends BaseActivity {
    private boolean CHECKED;
    private TextView textView, textView1, textView2, textView3, textView4, textView5,
            textView6, textView7, textView8, textView9, textView10;
    private Toolbar toolbar;
    private OEMProduct originality;
    private ImageView imageView;
    private ImageView imageView1;
    private ImageView imageView2;
    private ViewPager viewPager;
    private MyAdapter myAdapter;
    private String[] imgs = {"1"};
    private ArrayList<View> viewList;
    private String shareUrl;
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



        imageView1 = (ImageView) findViewById(R.id.iv_love);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CHECKED) {
                     deleteData();
                    imageView1.setImageResource(R.mipmap.love);
                    CHECKED = false;
                } else {
                     favorite();
                    imageView1.setImageResource(R.mipmap.love_fill);
                    CHECKED = true;

                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oemproduct_detail);
        initView();
        initData();
        initToolBar();
    }
    private void initData() {
        String stringExtra = getIntent().getStringExtra("made_id");
        Log.e("made_id", "made_id：" + stringExtra);
        Map<String, String> params = new HashMap<String, String>();
        params.put("made_id", String.valueOf(stringExtra));
        OkHttpUtils.post().url(GET_DAIGONF_PRODUCT_DETAIL).params(params).build().execute(new StringCallback() {
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
    /**
     * 收藏
     */
    private void favorite() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_type", "3");
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("c_u_account", originality.getAccount());
        params.put("c_from_id", originality.getMade_id());
        params.put("c_from_title", originality.getMade_title());
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
                        Snackbar.make(textView, "收藏成功！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(OEMProductDetailActivity.this, FavoriteActivity.class));
                            }
                        }).show();
                    else
                        Snackbar.make(textView, "您已经收藏过本条数据！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(OEMProductDetailActivity.this, FavoriteActivity.class));
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
        params.put("collection_id", originality.getMade_id());
        OkHttpUtils.post().params(params).url(NetWorkInterface.DELETE_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "收藏删除失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "删除收藏成功: " + response);

                Toast.makeText(OEMProductDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
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
//                    case R.id.update:
//                        Toast.makeText(OEMProductDetailActivity.this, "正在开发！",
//                                Toast.LENGTH_SHORT).show();
//                        break;
                    default:
                        break;
                }
                return true;
            }
        });
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
                .withTitle(originality.getMade_name())
                .withText(originality.getMade_describe())
                .withTargetUrl(NetWorkInterface.HOST+originality.getShare_url())
                .withMedia(new UMImage(this, R.drawable.sina_web_default))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        Toast.makeText(OEMProductDetailActivity.this, "分享已完成！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        Toast.makeText(OEMProductDetailActivity.this, "分享出错！",
                                Toast.LENGTH_SHORT).show();

                        Log.e("lgst", arg1.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        Toast.makeText(OEMProductDetailActivity.this, "分享已取消！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).open();
    }
    private void JsonToDatas(String response) {

        JSONObject jo = null;
        try {
            jo = new JSONObject(response);
            int status = jo.getInt("status");
            if (status == STATUS_SUCCESS) {
                String string = jo.getString("data");
                Log.e("weiwei", "weiwei" + string);
                originality = new Gson().fromJson(string, OEMProduct.class);

                if (originality != null) {
                    textView.setText(originality.getMade_name());
                    textView1.setText(originality.getMade_time());


                    textView4.setText(originality.getMade_describe());


                    textView8.setText(originality.getCollection_count());

                    //获取收藏状态
                    CHECKED = false;
                    //得到轮播图片
                    // 将要分页显示的View装入数组中
                    viewList = new ArrayList<View>();
                    imgs = originality.getMade_picture().split("\\,");
                    for (int i = 0; i < imgs.length; i++) {
                        Log.e("toJson", "所有图片的链接" + imgs[i] + imgs.length);
                        // NetWorkInterface.HOST + "/" + imgs[i]
                        if (!imgs[i].equals("null")) {
                            ImageView imageView = new ImageView(OEMProductDetailActivity.this);
                            Glide.with(this).load(NetWorkInterface.HOST + "/" + imgs[i]).into(imageView);
                            viewList.add(imageView);
                        }
                    }
                    if (viewList.size() == 0) {
                        textView6.setText("无");
                    } else {
                        textView6.setText("1/" + viewList.size());
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
        }
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
