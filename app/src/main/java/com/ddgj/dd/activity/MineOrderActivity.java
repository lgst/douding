package com.ddgj.dd.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ddgj.dd.R;
import com.ddgj.dd.fragment.MineOrdersFragment;

import java.util.ArrayList;
import java.util.List;

public class MineOrderActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ViewPager mVp;
    private List<Fragment> fragments;
    private String[] mTitles = {"待处理","已成功","已关闭"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_order);
        initData();
        initView();
    }

    private void initData() {
        // 1交易中 2交易成功 3交易失败 4取消订单 5申请验收 6拒绝验收 7确认合作 8拒绝合作
        fragments = new ArrayList<Fragment>();
        Fragment f0 = new MineOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("classes","");
        f0.setArguments(bundle);
        Fragment f1 = new MineOrdersFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("classes","");
        f1.setArguments(bundle1);
        Fragment f2 = new MineOrdersFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("classes","2");
        f2.setArguments(bundle2);
//        Fragment f3 = new MineOrdersFragment();
//        Bundle bundle3 = new Bundle();
//        bundle3.putString("classes","3");
//        f3.setArguments(bundle3);
        Fragment f4 = new MineOrdersFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putString("classes","4");
        f4.setArguments(bundle4);
        fragments.add(f1);
        fragments.add(f2);
//        fragments.add(f3);
        fragments.add(f4);
    }

    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("订单管理");
        mToolbar.setTitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("待处理"));
        tabLayout.addTab(tabLayout.newTab().setText("已成功"));
        tabLayout.addTab(tabLayout.newTab().setText("已关闭"));
        mVp = (ViewPager) findViewById(R.id.vp);
        mVp.setAdapter(new VpAdatpter(getSupportFragmentManager()));
        mVp.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(mVp);
    }

    class VpAdatpter extends FragmentPagerAdapter{

        public VpAdatpter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
