package com.ddgj.dd.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ddgj.dd.R;
import com.ddgj.dd.adapter.MainContentVPAdapter;
import com.ddgj.dd.fragment.CommunityFragment;
import com.ddgj.dd.fragment.HomeFragment;
import com.ddgj.dd.fragment.MessageFragment;
import com.ddgj.dd.fragment.MineFragment;
import com.ddgj.dd.receiver.NetReceiver;
import com.ddgj.dd.util.PermissionUtils;
import com.ddgj.dd.util.net.BusEvent;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CustomViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 * ViewPager（界面） + RadioButton（指示器）
 */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, NetWorkInterface, View.OnClickListener {
    private RadioGroup mRadioGroup;
    /**
     * 更新我的界面标志位
     */
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
    /**
     * 点击两次退出，第一次点击时间
     */
    private long backTime;
    private TextView mNotNet;
    private NetReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initView();
        initReceive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestLocationPermissions(this, 200)) {
                initLocation();
            }
        } else {
            initLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意给与权限  可以再此处调用拍照
                    initLocation();
                } else {
                    // f用户不同意 可以给一些友好的提示
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static void startToSettings(Context paramContext) {
        if (paramContext == null)
            return;
        try {
            if (Build.VERSION.SDK_INT > 10) {
                paramContext.startActivity(new Intent(
                        "android.settings.SETTINGS"));
                return;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
            return;
        }
        paramContext.startActivity(new Intent(
                "android.settings.WIRELESS_SETTINGS"));
    }

    private void initReceive() {
        EventBus.getDefault().register(this);
        mReceiver = new NetReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Subscribe
    public void onEventMainThread(BusEvent event) {
        if (event.what == BusEvent.NET_STATUS)
            setNetState(event.isConnect);
    }


    public void setNetState(boolean netState) {
        if (mNotNet != null) {
            mNotNet.setVisibility(netState ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * 初始化fragment
     */
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
        adapter = new MainContentVPAdapter(getSupportFragmentManager(), fragments);

    }

    public void initView() {
        customViewPager = (CustomViewPager) findViewById(R.id.act_main_content);
        mRadioGroup = (RadioGroup) findViewById(R.id.act_main_radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        customViewPager.setAdapter(adapter);
        customViewPager.setOffscreenPageLimit(3);//设置缓存页数，缓存所有fragment
        mNotNet = (TextView) findViewById(R.id.not_net);
        mNotNet.setOnClickListener(this);
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
        EventBus.getDefault().unregister(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.not_net:
                startToSettings(this);
                break;
        }
    }

    /**
     * 定位回调
     */
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int type = bdLocation.getLocType();
            String address = bdLocation.getAddrStr();
            String country = bdLocation.getCountry();
            String province = bdLocation.getProvince();
            String district = bdLocation.getDistrict();
            String street = bdLocation.getStreet();
            String streetNumbet = bdLocation.getStreetNumber();
            String city = bdLocation.getCity();
            city = city.substring(0, city.length() - 1);
            if (!city.isEmpty())
                getSharedPreferences("location", MODE_PRIVATE).edit().putString("city", city)
                        .putString("address", address)
                        .putString("country", country)
                        .putString("district", district)
                        .putString("province", province)
                        .putString("street", street)
                        .putString("streetNumber", streetNumbet).commit();
        }
    };

    public LocationClient mLocationClient = null;

    private void initLocation() {
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.start();
    }
}
