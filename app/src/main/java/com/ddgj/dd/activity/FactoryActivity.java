package com.ddgj.dd.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ddgj.dd.R;
import com.ddgj.dd.fragment.FactoryFragment;
import com.ddgj.dd.fragment.ProductFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/13/0013.
 */

public class FactoryActivity extends BaseActivity {
    private static final String[] TABS = {"工厂","创意产品"};
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        ViewPager mVp = (ViewPager) findViewById(R.id.vp);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        //tab模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(TABS[0]));
        tabLayout.addTab(tabLayout.newTab().setText(TABS[1]));
        //paper加载适配器
        tabLayout.setupWithViewPager(mVp);
        mFragments.add(new FactoryFragment());
        mFragments.add(new ProductFragment());
        mVp.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
    }

    public void backClick(View v) {
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return TABS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TABS[position];
        }
    }
}
