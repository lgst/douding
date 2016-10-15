package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.PersonalUser;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.user.UserHelper;

/**
 * Created by lg on 2016/10/8.
 * 个人用户信息
 */
public class PersonalFragment extends BaseFragment {
    private TextView name;
    private TextView gender;
    private TextView age;
    private TextView nickName;
    private TextView phoneNumber;
    private TextView email;
    private TextView address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center_personal, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }


    @Override
    protected void initViews() {
        name = (TextView) findViewById(R.id.name);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        nickName = (TextView) findViewById(R.id.nick_name);
        phoneNumber = (TextView) findViewById(R.id.phone_number);
        email = (TextView) findViewById(R.id.email);
        address = (TextView) findViewById(R.id.address);

        initDatas();
    }

    public void initDatas() {
        if(name==null)
        {//没有初始化控件不执行数据填充
            return;
        }
        PersonalUser user = (PersonalUser) UserHelper.getInstance().getUser();

        if (!user.getUser_name().isEmpty())
            name.setText(user.getUser_name());
        if(!user.getUser_sex().isEmpty())
            gender.setText(user.getUser_sex());
        if(!user.getUser_age().isEmpty())
        {
            String date = user.getUser_age();
            age.setText(String.valueOf(StringUtils.getAge(date)));
        }
        if(!user.getNickname().isEmpty())
            nickName.setText(user.getNickname());
        if(!user.getPhone_number().isEmpty())
            phoneNumber.setText(user.getPhone_number());
        if(!user.getUser_email().isEmpty())
            email.setText(user.getUser_email());
        if(!user.getUser_address().isEmpty())
            address.setText(user.getUser_address());
    }
}
