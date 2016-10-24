package com.ddgj.dd.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.AboutActivity;
import com.ddgj.dd.activity.LoginActivity;
import com.ddgj.dd.activity.MineProjectActivity;
import com.ddgj.dd.activity.SettingsActivity;
import com.ddgj.dd.activity.UserCenterActivity;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

import static com.ddgj.dd.activity.MainActivity.update;

/**
 * Created by Administrator on 2016/9/29.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener, NetWorkInterface {
    public static final int NEED_UPDATE_USERINFO = 200;
    private static final int REQUEST_CODE = 101;
    private CircleImageView userIcon;
    private TextView userName;
    private TextView userType;
    private TextView mOriginalityCount;
    private LinearLayout mOriginality;
    private TextView mPatentCount;
    private LinearLayout mPatent;
    private TextView mOrderCount;
    private LinearLayout mOrder;
    private TextView mOemCount;
    private LinearLayout mOem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (update)//更新我的界面
        {
            updateUserInfo();
            update = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        if (UserHelper.getInstance().isLogined()) {
            updateUserInfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    protected void initView() {
        //点击登录
        findViewById(R.id.click_login).setOnClickListener(this);
        //我的收藏
        findViewById(R.id.mine_favorite).setOnClickListener(this);
        //应用设置
        findViewById(R.id.app_settings).setOnClickListener(this);
        //分享
        findViewById(R.id.share_to_friend).setOnClickListener(this);
        //关于
        findViewById(R.id.about_us).setOnClickListener(this);
        //用户头像
        userIcon = (CircleImageView) findViewById(R.id.user_icon);
        //用户名
        userName = (TextView) findViewById(R.id.user_name);
        //用户身份
        userType = (TextView) findViewById(R.id.user_type);

        if (UserHelper.getInstance().getUser() != null &&
                UserHelper.getInstance().getUser().getAccount_type().equals("0")) {
            findViewById(R.id.oem).setVisibility(View.GONE);
        }
        findViewById(R.id.oem).setOnClickListener(this);
        mOemCount = (TextView) findViewById(R.id.oem_count);
        findViewById(R.id.order).setOnClickListener(this);
        mOrderCount = (TextView) findViewById(R.id.order_count);
        findViewById(R.id.originality).setOnClickListener(this);
        mOriginalityCount = (TextView) findViewById(R.id.originality_count);
        findViewById(R.id.patent).setOnClickListener(this);
        mPatentCount = (TextView) findViewById(R.id.patent_count);
        initData();
    }

    private void initData() {
        if (!UserHelper.getInstance().isLogined()) {
            return;
        }
        OkHttpUtils.get()
                .url(GET_MINE + "?account_id=" + UserHelper.getInstance().getUser().getAccount_id())
                .build().execute(new StringCallback() {
                                     @Override
                                     public void onError(Call call, Exception e, int id) {
                                         Log.i("lgst", "获取我的出错：" + e.getMessage());
                                     }

                                     @Override
                                     public void onResponse(String response, int id) {
                                         Log.i("lgst", response);
                                         try {
                                             JSONObject jsonObject = new JSONObject(response);
                                             int status = jsonObject.getInt("status");

                                             if (status == 0) {
                                                 String str = jsonObject.getString("data");
                                                 JSONObject jo = new JSONObject(str);
                                                 mOriginalityCount.setText(jo.getString("originality"));
                                                 mOemCount.setText(jo.getString("custom_made"));
                                                 mOemCount.setText(jo.getString("OEM"));
                                                 mPatentCount.setText(jo.getString("patent"));
                                             }
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }
        );
    }

    @Override
    public void onClick(View v) {
        int page = 0;
        switch (v.getId()) {
            case R.id.originality:

                break;
            case R.id.patent:
                page = 1;
                break;
            case R.id.order:
                page = 2;
                break;
            case R.id.oem:
                page = 3;
                break;
            case R.id.click_login:
                clickLogin();
                return;
            case R.id.mine_favorite:
                clcikMineFavorite();
                return;
            case R.id.app_settings:
                clickAppSettings();
                return;
            case R.id.share_to_friend:
                clickShare();
                return;
            case R.id.about_us:
                clickAboutUs();
                return;
        }
        if (UserHelper.getInstance().isLogined())
            startActivity(new Intent(getActivity(), MineProjectActivity.class).putExtra("page", page));
        else
            Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 点击关于
     */

    private void clickAboutUs() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    /**
     * 点击分享
     */
    private void clickShare() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE};
        new ShareAction(getActivity())
                .setDisplayList(displaylist)
                .withText("分享测试")
                .withTitle("豆丁工匠")
//                .withTargetUrl("")
                .withMedia(
                        new UMImage(getActivity(), R.drawable.sina_web_default))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        Toast.makeText(getActivity(), "分享已完成！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        Toast.makeText(getActivity(), "分享出错！",
                                Toast.LENGTH_SHORT).show();
                        arg1.printStackTrace();
                        Log.e("lgst", arg1.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        Toast.makeText(getActivity(), "分享已取消！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).open();
    }

    /**
     * 点击应用设置
     */
    private void clickAppSettings() {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
    }

    /**
     * 点击我的收藏
     */
    private void clcikMineFavorite() {
    }

    /**
     * 点击登录
     */
    private void clickLogin() {
        if (UserHelper.getInstance().isLogined()) {
            startActivity(new Intent(getActivity(), UserCenterActivity.class));
        } else
            startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("flag", LoginActivity.BACK));
    }

    public void updateUserInfo() {
        Object o = UserHelper.getInstance().getUser();
        if (o instanceof PersonalUser) {
            PersonalUser personalUser = (PersonalUser) o;
            String username = personalUser.getUser_name();
            if (username.equals("")) {
                username = personalUser.getAccount();
            }
            updateUI(username);
        } else if (o instanceof EnterpriseUser) {
            EnterpriseUser enterpriseUser = (EnterpriseUser) o;
            String enterprisename = enterpriseUser.getFacilitator_name();
            if (enterprisename.equals("")) {
                enterprisename = enterpriseUser.getAccount();
            }
            updateUI(enterprisename);
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        userName.setText(getResources().getString(R.string.click_login));
        userIcon.setImageResource(R.mipmap.ic_account_circle_white_48dp);
    }

    private void updateUI(String username) {
        userName.setText(username);
        Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getInstance().getmImageCache() + "user_icon");
        if (bitmap != null)
            userIcon.setImageBitmap(bitmap);
        else {
            Glide.with(this).load(NetWorkInterface.HOST + "/" + UserHelper.getInstance().getUser().getHead_picture())
                    .into(userIcon);
        }
    }
}
