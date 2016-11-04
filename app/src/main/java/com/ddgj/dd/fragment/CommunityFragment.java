package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.activity.LoginActivity;
import com.ddgj.dd.activity.PublishBBSActivity;
import com.ddgj.dd.util.user.UserHelper;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/9/29.
 */
public class CommunityFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<BaseFragment> fragments;
    private ArrayList<String> strings;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initDatas();
    }

    private void initDatas() {
        //初始化热门与板块fragment
        HotFragment hotFragment = new HotFragment();
        PlateFragment plateFragment = new PlateFragment();
        //裝入fragment
        fragments = new ArrayList<>();
        fragments.add(plateFragment);
        fragments.add(hotFragment);
        //初始化標題
        strings = new ArrayList<>();
        strings.add("全部");
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
    protected void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.vp_pager);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getInstance().isLogined()) {
                    Intent intent = new Intent(getActivity(), PublishBBSActivity.class);
                    startActivityForResult(intent, 3);
                } else {
                    ((BaseActivity)getActivity()).showToastShort("请先登录！");
                    startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("flag", LoginActivity.BACK));
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==100){
            ((PlateFragment)fragments.get(0)).postBeanList.clear();
            ((PlateFragment)fragments.get(0)).initdatas(1);
        }
    }
}
