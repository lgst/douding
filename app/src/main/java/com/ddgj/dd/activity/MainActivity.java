package com.ddgj.dd.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.MainContentVPAdapter;
import com.ddgj.dd.fragment.CommunityFragment;
import com.ddgj.dd.fragment.HomeFragment;
import com.ddgj.dd.fragment.MessageFragment;
import com.ddgj.dd.fragment.MineFragment;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 * ViewPager（界面） + RadioButton（指示器）
 * */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,NetWorkInterface {
    private RadioGroup mRadioGroup;
    /**更新我的界面标志位*/
    public static boolean update = false;
    private CustomViewPager customViewPager;
    private List<Fragment> fragments;
    private MainContentVPAdapter adapter;
    /**
     * 首页Fragment
     */
    private HomeFragment homeFragment;
    /**
     * 社区
     */
    private CommunityFragment communityFragment;
    /**
     * 消息
     */
    private MessageFragment messageFragment;
    /**
     * 我的
     */
    private MineFragment mineFragment;
    /**点击两次退出，第一次点击时间*/
    private long backTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initView();
//        new UpdateUtils(this).checkVersion();
    }


    /**初始化fragment*/
    private void initFragment() {
        homeFragment = new HomeFragment();
        communityFragment = new CommunityFragment();
        messageFragment = new MessageFragment();
        mineFragment = new MineFragment();
        fragments = new ArrayList<Fragment>();
        fragments.add(homeFragment);
        fragments.add(communityFragment);
        fragments.add(messageFragment);
        fragments.add(mineFragment);
        adapter = new MainContentVPAdapter(getSupportFragmentManager(),fragments);

    }

    public void initView() {
        customViewPager = (CustomViewPager) findViewById(R.id.act_main_content);
        mRadioGroup = (RadioGroup) findViewById(R.id.act_main_radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        customViewPager.setAdapter(adapter);
        customViewPager.setOffscreenPageLimit(3);//设置缓存页数，缓存所有fragment
    }

    /**
     * RadioGroup切换事件监听器
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.act_main_radio_btn_home://首页
                customViewPager.setCurrentItem(0);
                break;
            case R.id.act_main_radio_btn_community://社区
                customViewPager.setCurrentItem(1);
                break;
            case R.id.act_main_radio_btn_message://消息
                customViewPager.setCurrentItem(2);
                break;
            case R.id.act_main_radio_btn_mine://我的
                customViewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//点击两次退出，间隔最长时间2.5s
            long time = System.currentTimeMillis();
            if (time - backTime < 2500) {
                moveTaskToBack(true);
            } else {
                backTime = System.currentTimeMillis();
                showToastShort("再按一次返回键到桌面");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
