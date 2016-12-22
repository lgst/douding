package com.ddgj.dd.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ddgj.dd.bean.FavoriteInfo;
import com.ddgj.dd.bean.Order;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.RewardInfo;
import com.ddgj.dd.bean.RewardOrder;
import com.ddgj.dd.util.L;
import com.ddgj.dd.util.StringUtils;
import com.ddgj.dd.util.net.BusEvent;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.ddgj.dd.util.net.NetWorkInterface.GET_MINE_ORDER;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_MINE_ORIGINALITY;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_MINE_PATENT;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_MINE_REWARD;
import static com.ddgj.dd.util.net.NetWorkInterface.GET_MINE_REWARD_ORDER;

/**
 * Created by Administrator on 2016/12/14.
 */

public class InitService extends Service {

    private DbUtils mDbu;
    private ArrayList<Originality> mOriginalitys;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEventMainThread(BusEvent event) {
        switch (event.what) {
            case BusEvent.LOGIN:
                init();
                break;
            case BusEvent.ORI:
                initMineOri();
                break;
            case BusEvent.PATENT:
                initMinePatent();
                break;
            case BusEvent.ORDER:
                initMineOrder();
                break;
            case BusEvent.OEM:
                initMineOem();
                break;
            case BusEvent.ORDERS:
                initMineOrders();
                break;
            case BusEvent.REWARD:
                initMineReward();
                break;
            case BusEvent.TENDER:
                initMineTender();
                break;
            case BusEvent.FAVORIT:
//                initMineFavorit();
                init();
                break;
            case BusEvent.REWARD_ORDER:
                initMineRewardOrder();
                break;
        }
    }

    /**
     * 初始化个人数据
     */
    private void init() {
        if (!UserHelper.getInstance().isLogined())
            return;

        L.i("initstart");
        DbUtils.DaoConfig config = new DbUtils.DaoConfig(this);
        config.setDbName(StringUtils.getDbName());
        config.setDbVersion(1);
        mDbu = DbUtils.create(config);
//        initMineOri();//初始化我的创意
//        initMinePatent();//初始化我的专利
//        initMineOrder();//初始化我的订制
//        initMineOem();//初始化我的代工
//        initMinePublish();//初始化我的采购
//        initMineReward();//初始化我的悬赏
//        initMineRewardOrder();//初始化我参与的悬赏
//        initMineTender();//初始化我的招标
//        initMineOrders();//初始化我的订单
        initMineFavorit();//初始化我的收藏

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMineFavorit() {
        try {
            mDbu.deleteAll(FavoriteInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("c_u_id", UserHelper.getInstance().getUser().getAccount_id());
        params.put("pageNumber", "1");
        params.put("pageSingle", "1000");
        OkHttpUtils.post().params(params).url(NetWorkInterface.GET_FAVORITE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("查询我的收藏失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                L.i("onResponse: " + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
//                        List<FavoriteInfo> mFavorites = mDbu.findAll(FavoriteInfo.class);
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String json = ja.getJSONObject(i).toString();
                            FavoriteInfo info = new Gson().fromJson(json, FavoriteInfo.class);
//                            if (mFavorites == null || !mFavorites.contains(info))
                            mDbu.save(info);
                        }
//                        stopSelf();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initMineOrders() {
    }

    private void initMineTender() {
    }

    private void initMineReward() {
        OkHttpUtils.get().url(GET_MINE_REWARD + "?reward_u_id=" + UserHelper.getInstance().getUser().getAccount_id()
                + "&pageNumber=1&pageSingle=10000000")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("加载" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                L.i("加载我的悬赏成功：" + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        List<RewardInfo> cache = mDbu.findAll(RewardInfo.class);
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            RewardInfo reward = new Gson().fromJson(str, RewardInfo.class);
                            if (cache == null || cache.contains(reward))
                                mDbu.save(reward);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initMineRewardOrder() {
        OkHttpUtils.get().url(GET_MINE_REWARD_ORDER + "?reward_u_id=" +
                UserHelper.getInstance().getUser().getAccount_id()
                + "&pageNumber=1&pageSingle=1000000000")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                L.e("获取参与的悬赏失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                L.i("获取我参与的悬赏成功：" + response);
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getInt("status") == 0) {
                        JSONArray ja = jo.getJSONArray("data");
                        List<RewardOrder> cache = mDbu.findAll(RewardOrder.class);
                        for (int i = 0; i < ja.length(); i++) {
                            String str = ja.getString(i);
                            RewardOrder ro = new Gson().fromJson(str, RewardOrder.class);
                            if (cache == null || !cache.contains(ro))
                                mDbu.save(ro);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initMinePublish() {
    }

    private void initMineOem() {
    }

    /**
     * 更新本地个人定制数据
     */
    private void initMineOrder() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "10000000");
        params.put("made_differentiate", "0");
        params.put("m_a_id", UserHelper.getInstance().getUser().getAccount_id());
        new HttpHelper<Order>(this, Order.class)
                .getDatasPost(GET_MINE_ORDER, params, new DataCallback<Order>() {
                    @Override
                    public void Failed(Exception e) {
                        Log.e("lgst", "我的订制获取出错：" + e.getMessage());
                    }

                    @Override
                    public void Success(List<Order> datas) {
                        L.i("获取我的订制成功！");
                        try {
                            List<Order> orders = mDbu.findAll(Order.class);
                            if (orders == null)
                                mDbu.saveAll(datas);
                            else
                                for (Order order : datas) {
                                    if (!orders.contains(order))
                                        mDbu.delete(order);
                                }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 更新本地个人专利数据
     */
    private void initMinePatent() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf("1"));
        params.put("pageSingle", "10000000");
        params.put("p_account_id", UserHelper.getInstance().getUser().getAccount_id());
        new HttpHelper<Patent>(this, Patent.class)
                .getDatasPost(GET_MINE_PATENT, params, new DataCallback<Patent>() {
                    @Override
                    public void Failed(Exception e) {
                        Log.e("lgst", "我的专利加载失败！" + e.getMessage());
                    }

                    @Override
                    public void Success(List<Patent> datas) {
                        L.i("加载我的专利成功！");
                        try {
                            List<Patent> patents = mDbu.findAll(Patent.class);
                            for (Patent patent : datas) {
                                if (patents == null || !patents.contains(patent)) {
                                    mDbu.save(patent);
                                }
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 更新本地个人创意数据
     */
    private void initMineOri() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", String.valueOf("1"));
        params.put("pageSingle", "1000000000");
        params.put("o_account_id", UserHelper.getInstance().getUser().getAccount_id());
        new HttpHelper<Originality>(this, Originality.class)
                .getDatasPost(GET_MINE_ORIGINALITY, params, new DataCallback<Originality>() {
                    @Override
                    public void Failed(Exception e) {
                        L.e("获取我的创意失败！网络出错：" + e.getMessage());
                    }

                    @Override
                    public void Success(List<Originality> datas) {
                        L.i("获取我的创意成功！");
                        try {
                            List<Originality> oris = mDbu.findAll(Originality.class);
                            for (Originality ori : datas) {
                                if (oris == null || !oris.contains(ori))
                                    mDbu.save(ori);
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mDbu.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
