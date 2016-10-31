package com.ddgj.dd.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.camera.CameraActivity;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;

import static com.ddgj.dd.util.net.HttpHelper.uploadUserIcon;

/**
 * Created by lg on 2016/10/8.
 * 个人用户信息
 */
public class PersonalFragment extends BaseFragment implements NetWorkInterface{
    private TextView name;
    private TextView gender;
    private TextView age;
    private TextView nickName;
    private TextView phoneNumber;
    private TextView email;
    private TextView address;
    private TextView account;
    private LinearLayout setIcon;
    private CircleImageView mIcon;
    private String mIconPath;
    private static final int REQUEST_CODE = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center_personal, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIconPath = FileUtil.getInstance().getmImageCache() + "user_icon";
        initView();
        initDatas();
    }


    @Override
    protected void initView() {
        name = (TextView) findViewById(R.id.name);
        account = (TextView) findViewById(R.id.account);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        nickName = (TextView) findViewById(R.id.nick_name);
        phoneNumber = (TextView) findViewById(R.id.phone_number);
        email = (TextView) findViewById(R.id.email);
        address = (TextView) findViewById(R.id.address);
        mIcon = (CircleImageView) findViewById(R.id.icon);
        setIcon = (LinearLayout) findViewById(R.id.ll_icon);
        setIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("scaleType", true).putExtra("path",mIconPath),REQUEST_CODE);
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(mIconPath);
        if (bitmap != null)
            mIcon.setImageBitmap(bitmap);
        else
            Glide.with(this).load(NetWorkInterface.HOST + "/" + UserHelper.getInstance().getUser().getHead_picture())
                    .into(mIcon);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == BaseActivity.SUCCESS) {
            Bitmap bitmap = BitmapFactory.decodeFile(mIconPath);
            if (bitmap != null) {
                mIcon.setImageBitmap(bitmap);
                uploadUserIcon(getActivity(),mIconPath);
            }
        }
    }



    public void initDatas() {
        if (name == null) {//没有初始化控件不执行数据填充
            return;
        }
        PersonalUser user = (PersonalUser) UserHelper.getInstance().getUser();

        if (!user.getUser_name().isEmpty())
            name.setText(user.getUser_name());
        if (!user.getUser_sex().isEmpty())
            gender.setText(user.getUser_sex());
        if (!user.getUser_age().isEmpty()) {
            String date = user.getUser_age();
            age.setText(String.valueOf(StringUtils.getAge(date)));
        }
        if (!user.getNickname().isEmpty())
            nickName.setText(user.getNickname());
        if (!user.getPhone_number().isEmpty())
            phoneNumber.setText(user.getPhone_number());
        if (!user.getUser_email().isEmpty())
            email.setText(user.getUser_email());
        if (!user.getUser_address().isEmpty())
            address.setText(user.getUser_address());
        account.setText(user.getAccount());
    }
}
