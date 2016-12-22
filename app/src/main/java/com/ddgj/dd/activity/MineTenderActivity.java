package com.ddgj.dd.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ddgj.dd.R;
import com.ddgj.dd.fragment.MTenderListFragment;
import com.ddgj.dd.fragment.MTenderOrderFragment;

import java.util.ArrayList;
import java.util.List;

public class MineTenderActivity extends BaseActivity {

    private Toolbar mToolbar;
    private TabLayout mTabBar;
    private ViewPager mVp;
    private List<Fragment> mFragments = new ArrayList<>();
    private static final String TITLES[] = {"招标", "投标"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mime_tender);
        initData();
        initView();
    }

    private void initData() {
        mFragments.add(new MTenderListFragment());
        mFragments.add(new MTenderOrderFragment());
    }

    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitle("招标管理");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTabBar = (TabLayout) findViewById(R.id.tab_bar);
        initTab();
        mVp = (ViewPager) findViewById(R.id.vp);
        mVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return TITLES[position];
            }
        });
        mTabBar.setupWithViewPager(mVp);
    }

    private void initTab() {
        mTabBar.addTab(mTabBar.newTab().setText(TITLES[0]));
        mTabBar.addTab(mTabBar.newTab().setText(TITLES[1]));
    }

}
