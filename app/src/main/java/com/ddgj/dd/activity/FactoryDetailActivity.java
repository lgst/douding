package com.ddgj.dd.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.Factory;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.User;
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

import static com.ddgj.dd.util.net.NetWorkInterface.GET_FACTORY_DETAIL;
import static com.ddgj.dd.util.net.NetWorkInterface.STATUS_SUCCESS;

public class FactoryDetailActivity extends BaseActivity {

    private TextView mName;
    private TextView mType;
    private TextView mContact;
    private TextView mAddress;
    private TextView mField;
    private TextView mProfile;
    private TextView mNumber;
    private ImageView mHeadPic;
    private ImageView mLicensepic;
    private ImageView mlove;
    private LinearLayout mFacilitatorPic;
    private Factory mFactory;
    private boolean CHECKED;
    private String[] imgs = {"1"};
    private TextView mTvLicense;
    private Toolbar toolbar;

    @Override
    protected void initView() {
        mName = (TextView) findViewById(R.id.tv_facilitator_name);
        mType = (TextView) findViewById(R.id.tv_facilitator_type);
        mContact = (TextView) findViewById(R.id.tv_facilitator_contact);
        mAddress = (TextView) findViewById(R.id.tv_facilitator_address);
        mField = (TextView) findViewById(R.id.tv_facilitator_field);
        mProfile = (TextView) findViewById(R.id.tv_factory_profile);
        mNumber = (TextView) findViewById(R.id.tv_collect_number);
        mTvLicense = (TextView) findViewById(R.id.tv_facilitator_license);

        mHeadPic = (ImageView) findViewById(R.id.head_pic);
        mLicensepic = (ImageView) findViewById(R.id.iv_facilitator_license);
        mlove = (ImageView) findViewById(R.id.iv_love);
        mlove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CHECKED) {
                    deleteData();
                    mlove.setImageResource(R.mipmap.love);
                    CHECKED = false;
                } else {
                    favorite();
                    mlove.setImageResource(R.mipmap.love_fill);
                    CHECKED = true;

                }

            }
        });

        mFacilitatorPic = (LinearLayout) findViewById(R.id.ll_facilitator_picture);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory_detail);
        initToolBar();
        initView();
        initData();

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
                        Toast.makeText(FactoryDetailActivity.this, "正在开发！",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    /**
     * 收藏
     */
    private void favorite() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection_type", "6");
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("c_u_account", mFactory.getAccount());
        params.put("c_from_id", mFactory.getAcilitator_id());
        params.put("c_from_title", mFactory.getFacilitator_name());
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
                        Snackbar.make(toolbar, "收藏成功！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(FactoryDetailActivity.this, FavoriteActivity.class));
                            }
                        }).show();
                    else
                        Snackbar.make(toolbar, "您已经收藏过本条数据！", Snackbar.LENGTH_LONG).setAction("查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(FactoryDetailActivity.this, FavoriteActivity.class));
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
        params.put("c_from_id", mFactory.getAcilitator_id());
        OkHttpUtils.post().params(params).url(NetWorkInterface.QUXIAO_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "收藏删除失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG, "取消收藏成功: " + response);

                Toast.makeText(FactoryDetailActivity.this, "取消成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initData() {
        String stringExtra = getIntent().getStringExtra("acilitator_id");
        Log.e("acilitator_id", "originality_id：" + stringExtra);
        Map<String, String> params = new HashMap<String, String>();
        params.put("acilitator_id", String.valueOf(stringExtra));
        OkHttpUtils.post().url(GET_FACTORY_DETAIL).params(params).build().execute(new StringCallback() {
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
     * 分享
     */
    private void share() {

        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE};
        new ShareAction(this)
                .setDisplayList(displaylist)
                .withTitle(mFactory.getFacilitator_name())
                .withText(mFactory.getFactory_profile())
                .withTargetUrl(NetWorkInterface.HOST+mFactory.getShare_url())
                .withMedia(new UMImage(this, R.drawable.sina_web_default))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        Toast.makeText(FactoryDetailActivity.this, "分享已完成！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        Toast.makeText(FactoryDetailActivity.this, "分享出错！",
                                Toast.LENGTH_SHORT).show();

                        Log.e("lgst", arg1.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        Toast.makeText(FactoryDetailActivity.this, "分享已取消！",
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
                mFactory = new Gson().fromJson(string, Factory.class);

                if (mFactory != null) {
                    mName.setText(mFactory.getFacilitator_name());
                    mType.setText(mFactory.getFacilitator_type());
                    mContact.setText(mFactory.getFacilitator_contact());
                    mAddress.setText(mFactory.getFacilitator_address());
                    mField.setText(mFactory.getFacilitator_field());
                    mProfile.setText(mFactory.getFactory_profile());
                    mNumber.setText(mFactory.getCollection_count());
                    //获取收藏状态
                    if (mFactory.getCollection_state() != null) {
                        if (mFactory.getCollection_state().equals("1")) {
                            CHECKED = true;
                            mlove.setImageResource(R.mipmap.love_fill);
                        } else {
                            CHECKED = false;
                        }
                    } else {
                        CHECKED = false;
                    }
                    if (mFactory.getHead_picture() != null) {
                        Glide.with(this).load(NetWorkInterface.HOST + "/" + mFactory.getHead_picture()).into(mHeadPic);
                    }
                    if (mFactory.getFacilitator_license() != null) {
                        Glide.with(this).load(NetWorkInterface.HOST + "/" + mFactory.getHead_picture()).into(mLicensepic);
                    }else {
                        mTvLicense.setVisibility(View.VISIBLE);
                    }

                    if (mFactory.getFacilitator_picture() != null) {

                        imgs = mFactory.getFacilitator_picture().split("\\,");
                        for (int i = 0; i < imgs.length; i++) {
                            Log.e("toJson", "所有图片的链接" + imgs[i] + imgs.length);
                            // NetWorkInterface.HOST + "/" + imgs[i]
                            if (!imgs[i].equals("null")) {
                                ImageView imageView = new ImageView(FactoryDetailActivity.this);
                                Glide.with(this).load(NetWorkInterface.HOST + "/" + imgs[i]).into(imageView);
                                mFacilitatorPic.addView(imageView);
                            }
                        }

                    }



                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
        }

    }
}
