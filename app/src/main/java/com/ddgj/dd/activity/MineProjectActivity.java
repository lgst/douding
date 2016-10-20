package com.ddgj.dd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.ListVPAdapter;
import com.ddgj.dd.fragment.MineOriginalityFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级页面，六个分类共用，需要传入参数：page 0-6 ， 0：全部 ， 1-6：分类
 * */
public class MineProjectActivity extends BaseActivity {
    private ViewPager mViewPager;
    private MagicIndicator magicIndicator;
    private String[] indicatorItems;
    List<Fragment> framgents  = new ArrayList<Fragment>();
    private FragmentStatePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initDatas();
        initFragments();
        initIndicator();
        init();
    }

    private void init() {
        Intent intent = getIntent();
        int page = intent.getIntExtra("page",0);
        mViewPager.setCurrentItem(page);
    }

    private void initDatas() {
        indicatorItems = getResources().getStringArray(R.array.mine_classes);
    }

    private void initFragments() {
        framgents.add(new MineOriginalityFragment());
        framgents.add(new MineOriginalityFragment());
        framgents.add(new MineOriginalityFragment());
        framgents.add(new MineOriginalityFragment());
        adapter = new ListVPAdapter(getSupportFragmentManager(),framgents);
        mViewPager.setAdapter(adapter);
    }

    private void initIndicator() {
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return indicatorItems.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(indicatorItems[index]);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.grey));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerPadding(UIUtil.dip2px(this, 15));
        titleContainer.setDividerDrawable(getResources().getDrawable(R.drawable.simple_splitter));
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @Override
    public void initView() {
        mViewPager = (ViewPager) findViewById(R.id.list_vp);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
    }

    public void backClick(View v)
    {
        finish();
    }
}
