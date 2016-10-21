package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddgj.dd.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/9/29.
 */
public class CommunityFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<BaseFragment> fragments;
    private ArrayList<String> strings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initDatas();
    }

    private void initDatas() {
        //初始化热门与板块fragment
        HotFragment hotFragment = new HotFragment();
        PlateFragment plateFragment = new PlateFragment();
        //裝入fragment
        fragments = new ArrayList<>();
        fragments.add(hotFragment);
        fragments.add(plateFragment);
        //初始化標題
        strings = new ArrayList<>();
        strings.add("所有");
        strings.add("热门");
        //tab模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(strings.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(strings.get(1)));
        //paper加载适配器
        TabAdapter tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void initViews() {
        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.vp_pager);
    }

    /**
     * paper适配器
     */
    class TabAdapter extends FragmentPagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return strings.size();
        }

        //此方法用来显示tab上的名字
        @Override
        public CharSequence getPageTitle(int position) {

            return strings.get(position % strings.size());
        }
    }
}
