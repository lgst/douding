package com.ddgj.dd;

import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.ddgj.dd.db.DBManager;
import com.ddgj.dd.util.CrashHandler;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.user.UserHelper;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by Administrator on 2016/9/28.
 */
public class DDGJApplication extends Application {

    private DBManager dbHelper;

    /**
     * 友盟分享集成
     */
    //各个平台的配置，建议放在全局Application或者程序入口
    {
        Config.REDIRECT_URL = "http://www.qsztx.com";
        //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin("wxb62478d5d6955e1c", "20a2c4b2342614f9cc69c88b0d8b72d7");
        //新浪微博
        PlatformConfig.setSinaWeibo("4227802417", "648d630d8f9126172e64077c6bf383be");
        //QQ、QZone
        PlatformConfig.setQQZone("1105437477", "VFjUid5UIhMt9FoD");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //导入数据库
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        UserHelper.getInstance().initUserInfo(this);//用户初始化
        FileUtil.getInstance().init(getApplicationContext());//目录初始化
        SDKInitializer.initialize(getApplicationContext());//百度地图初始化
        initLocation();
        initEM();//环信easeui初始化
        initOkhttp();//OKhttp初始化
//        bug追踪初始化
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        HttpHelper.uploadError();
    }

    /**
     * 配置网络请求
     */
    private void initOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 环信初始化
     */
    private void initEM() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);
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
