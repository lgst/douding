package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.user.UserHelper;

/**
 * Created by Administrator on 2016/10/11.
 */
public class EnterpriseFragment extends BaseFragment {
    /**
     * 账号
     */
    private TextView enterpriseId;
    /**
     * 企业名称
     */
    private TextView enterpriseName;
    /**
     * 规模
     */
    private TextView enterpriseScale;
    /**
     * 领域
     */
    private TextView enterpriseField;
    /**
     * 区域
     */
    private TextView enterpriseArea;
    /**
     * 地址
     */
    private TextView enterpriseAddress;
    /**
     * 邮箱
     */
    private TextView enterpriseEmail;
    /**
     * 联系人
     */
    private TextView enterpriseLinkName;
    /**
     * 联系方式
     */
    private TextView enterpriseContact;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center_enterprise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initDatas();
    }

    private void initDatas() {
        EnterpriseUser user = (EnterpriseUser) UserHelper.getInstance().getUser();
        if(user==null)
            return;
        enterpriseId.setText(user.getAccount());
        if (!user.getFacilitator_name().isEmpty())
            enterpriseName.setText(user.getFacilitator_name());
        if (!user.getFacilitator_scale().isEmpty())
            enterpriseScale.setText(user.getFacilitator_scale());
        if(!user.getFacilitator_field().isEmpty())
            enterpriseField.setText(user.getFacilitator_field());
        if(!user.getFacilitator_area().isEmpty())
            enterpriseArea.setText(user.getFacilitator_area());
        if(!user.getFacilitator_address().isEmpty())
            enterpriseAddress.setText(user.getFacilitator_address());
        if(!user.getFacilitator_email().isEmpty())
            enterpriseEmail.setText(user.getFacilitator_email());
        if(!user.getFacilitator_linkman().isEmpty())
            enterpriseLinkName.setText(user.getFacilitator_linkman());
        if(!user.getFacilitator_contact().isEmpty())
            enterpriseContact.setText(user.getFacilitator_contact());
    }

    @Override
    protected void initViews() {
        enterpriseId = (TextView) findViewById(R.id.enterprise_id);
        enterpriseName = (TextView) findViewById(R.id.enterprise_name);
        enterpriseScale = (TextView) findViewById(R.id.enterprise_scale);
        enterpriseField = (TextView) findViewById(R.id.enterprise_field);
        enterpriseArea = (TextView) findViewById(R.id.enterprise_area);
        enterpriseAddress = (TextView) findViewById(R.id.enterprise_address);
        enterpriseEmail = (TextView) findViewById(R.id.enterprise_link_name);
        enterpriseLinkName = (TextView) findViewById(R.id.enterprise_link_name);
        enterpriseContact = (TextView) findViewById(R.id.enterprise_contact);
    }
}
