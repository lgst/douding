package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.AboutActivity;
import com.ddgj.dd.activity.LoginActivity;
import com.ddgj.dd.activity.UserCenterActivity;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;

/**
 * Created by Administrator on 2016/9/29.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {
    public static final int NEED_UPDATE_USERINFO = 200;
    private static final int REQUEST_CODE = 101;
    private CircleImageView userIcon;
    private TextView userName;
    private TextView userType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
        if (UserHelper.getInstance().isLogined()) {
            updateUserInfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    protected void initViews() {
        //点击登录
        findViewById(R.id.click_login).setOnClickListener(this);
        //个人中心
        findViewById(R.id.personal_center).setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_login:
                clickLogin();
                break;
            case R.id.personal_center:
                clickPersonalCenter();
                break;
            case R.id.mine_favorite:
                clcikMineFavorite();
                break;
            case R.id.app_settings:
                clickAppSettings();
                break;
            case R.id.share_to_friend:
                clickShare();
                break;
            case R.id.about_us:
                clickAboutUs();
                break;
        }
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
    }

    /**
     * 点击应用设置
     */
    private void clickAppSettings() {
    }

    /**
     * 点击我的收藏
     */
    private void clcikMineFavorite() {
    }

    /**
     * 点击个人中心
     */
    private void clickPersonalCenter() {
        if (UserHelper.getInstance().isLogined()) {
            startActivity(new Intent(getActivity(), UserCenterActivity.class));
        } else {
            Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击登录
     */
    private void clickLogin() {
        if (UserHelper.getInstance().isLogined()) {
            return;
        }
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
    }

    private void updateUI(String username) {
        userName.setText(username);
    }
}
