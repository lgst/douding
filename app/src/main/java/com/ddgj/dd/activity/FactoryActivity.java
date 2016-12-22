package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ddgj.dd.R;
import com.ddgj.dd.fragment.FactoryFragment;
import com.ddgj.dd.fragment.ProductFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/13/0013.
 */

public class FactoryActivity extends BaseActivity implements View.OnClickListener {
    private static final String[] TABS = {"工厂", "创意产品"};
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private ViewPager mVp;
    private TextView mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        initView();
    }

    /**
     * 初始化控件
     */
    public void initView() {
        mVp = (ViewPager) findViewById(R.id.vp);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        //tab模式
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        tabLayout.addTab(tabLayout.newTab().setText(TABS[0]));
//        tabLayout.addTab(tabLayout.newTab().setText(TABS[1]));
        //paper加载适配器
//        tabLayout.setupWithViewPager(mVp);
        mFragments.add(new FactoryFragment());
//        mFragments.add(new ProductFragment());
        mVp.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearch = (TextView) findViewById(R.id.search);
        mSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                startActivityForResult(new Intent(this, SearchActivity.class), 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SUCCESS&&requestCode==1) {
            int currentItem = mVp.getCurrentItem();
            String key = data.getStringExtra("content");
            mSearch.setText(key);
            if (currentItem == 0) {//豆丁
                ((FactoryFragment) mFragments.get(0)).setmKeyWords(key).search();
            } else {
                ((ProductFragment) mFragments.get(1)).setmKeyWords(key).search();
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return TABS[position];
//        }
    }
}
