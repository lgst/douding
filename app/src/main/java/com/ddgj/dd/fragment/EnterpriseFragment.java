package com.ddgj.dd.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.bean.EnterpriseUser;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.camera.CameraActivity;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CircleImageView;

import static com.ddgj.dd.util.net.HttpHelper.uploadUserIcon;

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
    private CircleImageView mIcon;
    private String mIconPath;
    private LinearLayout setIcon;
    private static final int REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center_enterprise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIconPath = FileUtil.getInstance().getmImageCache() + "user_icon";
        initView();
        initDatas();
    }

    private void initDatas() {
        EnterpriseUser user = (EnterpriseUser) UserHelper.getInstance().getUser();
        if (user == null)
            return;
        enterpriseId.setText(user.getAccount());
        if (!user.getFacilitator_name().isEmpty())
            enterpriseName.setText(user.getFacilitator_name());
        if (!user.getFacilitator_scale().isEmpty())
            enterpriseScale.setText(user.getFacilitator_scale());
        if (!user.getFacilitator_field().isEmpty())
            enterpriseField.setText(user.getFacilitator_field());
        if (!user.getFacilitator_area().isEmpty())
            enterpriseArea.setText(user.getFacilitator_area());
        if (!user.getFacilitator_address().isEmpty())
            enterpriseAddress.setText(user.getFacilitator_address());
        if (!user.getFacilitator_email().isEmpty())
            enterpriseEmail.setText(user.getFacilitator_email());
        if (!user.getFacilitator_linkman().isEmpty())
            enterpriseLinkName.setText(user.getFacilitator_linkman());
        if (!user.getFacilitator_contact().isEmpty())
            enterpriseContact.setText(user.getFacilitator_contact());
    }

    @Override
    protected void initView() {
        enterpriseId = (TextView) findViewById(R.id.enterprise_id);
        enterpriseName = (TextView) findViewById(R.id.enterprise_name);
        enterpriseScale = (TextView) findViewById(R.id.enterprise_scale);
        enterpriseField = (TextView) findViewById(R.id.enterprise_field);
        enterpriseArea = (TextView) findViewById(R.id.enterprise_area);
        enterpriseAddress = (TextView) findViewById(R.id.enterprise_address);
        enterpriseEmail = (TextView) findViewById(R.id.enterprise_email);
        enterpriseLinkName = (TextView) findViewById(R.id.enterprise_link_name);
        enterpriseContact = (TextView) findViewById(R.id.enterprise_contact);
        mIcon = (CircleImageView) findViewById(R.id.icon);
        setIcon = (LinearLayout) findViewById(R.id.ll_icon);
        setIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(),
                        CameraActivity.class)
                        .putExtra("scaleType", true)
                        .putExtra("path", mIconPath)
                        ,REQUEST_CODE);
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(mIconPath);
        if (bitmap != null) {
            mIcon.setImageBitmap(bitmap);
        }else{
            Glide.with(this).load(NetWorkInterface.HOST + "/" + UserHelper.getInstance().getUser().getHead_picture())
                    .into(mIcon);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == BaseActivity.SUCCESS) {
            Bitmap bitmap = BitmapFactory.decodeFile(mIconPath);
            if (bitmap != null) {
                mIcon.setImageBitmap(bitmap);
                uploadUserIcon(getActivity(), mIconPath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
