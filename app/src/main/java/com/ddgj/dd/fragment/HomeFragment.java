package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.adapter.ADAdapter;
import com.ddgj.dd.adapter.ClassesGridViewAdapter;
import com.ddgj.dd.adapter.OriginalityPLVAdapter;
import com.ddgj.dd.adapter.PatentPLVAdapter;
import com.ddgj.dd.bean.ADBean;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;
import com.google.gson.Gson;
import com.hejunlin.superindicatorlibray.CircleIndicator;
import com.hejunlin.superindicatorlibray.LoopViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

//import org.xutils.DbManager;
//import org.xutils.ex.DbException;
//import org.xutils.x;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class HomeFragment extends BaseFragment implements NetWorkInterface {
    private BaseActivity act;
    private SwipeRefreshLayout refreshLayout;
    private LoopViewPager viewpager;
    private CircleIndicator indicator;
    private CustomGridView classesGV;
    private CustomListView patentListView;
    private CustomListView originalityListView;
//    private DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
//            .setDbName("home_chach.db")
//            .setDbVersion(2)
//            .setDbOpenListener(new DbManager.DbOpenListener() {
//                @Override
//                public void onDbOpened(DbManager db) {
//                    // 开启WAL, 对写入加速提升巨大
//                    db.getDatabase().enableWriteAheadLogging();
//                }
//            });
//    private DbManager dm = x.getDb(daoConfig);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initChach();
        initAD();
        initPatent();
        initOriginality();
    }

    private void initChach() {
//        try {
//            List<ADBean> ads = dm.findAll(ADBean.class);
//            viewpager.setAdapter(new ADAdapter(act, ads));
//            viewpager.setLooperPic(true);//是否设置自动轮播
//            indicator.setViewPager(viewpager);
//            List<Originality> mOriginalitys = dm.findAll(Originality.class);
//            originalityListView.setAdapter(new OriginalityPLVAdapter(act, mOriginalitys));
//            List<Patent> mPatents = dm.findAll(Patent.class);
//            patentListView.setAdapter(new PatentPLVAdapter(act, mPatents));
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 初始化创意列表
     */
    private void initOriginality() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "5");
        OkHttpUtils.post().url(GET_HOT_ORIGINALITY).params(params).build().execute(
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        act.showToastNotNetWork();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            if (status == STATUS_SUCCESS) {
//                                dm.delete(Originality.class);//清除上次缓存数据
                                List<Originality> mOriginalitys = new ArrayList<Originality>();
                                JSONArray ja = jo.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    String patentStr = ja.getJSONObject(i).toString();
                                    Originality originality = new Gson().fromJson(patentStr, Originality.class);
                                    mOriginalitys.add(originality);
//                                    dm.save(originality);//缓存创意实体类
                                }
//                                dm.close();
                                originalityListView.setAdapter(new OriginalityPLVAdapter(act, mOriginalitys));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } /*catch (DbException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                    }
                }
        );
    }

    /**
     * 初始化专利列表
     */
    private void initPatent() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "5");
        OkHttpUtils.post().url(GET_HOT_PATENT).params(params).build().execute(
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("lgst", "首页专利加载出错：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            List<Patent> mPatents = new ArrayList<Patent>();
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            if (status == STATUS_SUCCESS) {
//                                dm.delete(Patent.class);//清除上次缓存
                                JSONArray ja = jo.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    String patentStr = ja.getJSONObject(i).toString();
                                    Patent patent = new Gson().fromJson(patentStr, Patent.class);
                                    mPatents.add(patent);
//                                    dm.save(patent);//缓存实专利体类对象
                                }
//                                dm.close();//关闭数据库
                                patentListView.setAdapter(new PatentPLVAdapter(act, mPatents));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } /*catch (DbException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                }

        );
    }


    @Override
    protected void initViews() {
//        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), SearchActivity.class));
//            }
//        });
        viewpager = (LoopViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        classesGV = (CustomGridView) findViewById(R.id.calsses_list);
        patentListView = (CustomListView) findViewById(R.id.patent_list);
        originalityListView = (CustomListView) findViewById(R.id.originality_list);
        classesGV.setAdapter(new ClassesGridViewAdapter(act));
    }

    /**
     * 初始化轮播图
     */
    private void initAD() {
        OkHttpUtils.post().url(NetWorkInterface.GET_AD).id(100).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i("lgst", "获取轮播图失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jo = new JSONObject(response);
                    if (STATUS_SUCCESS == jo.getInt("status")) {
//                        dm.delete(ADBean.class);//清除缓存实体类
                        JSONArray array = jo.getJSONArray("data");
                        List<ADBean> adBeens = new ArrayList<ADBean>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            ADBean adBean = new Gson().fromJson(jsonObject.toString(), ADBean.class);
                            adBeens.add(adBean);
                            ImageView imageView = (ImageView) act.getLayoutInflater().inflate(R.layout.item_home_list_ad_item, null);
                            Glide.with(act)
                                    .load(HOST + "/" + adBean.getPicture())
                                    .thumbnail(0.1f)
                                    .into(imageView);
//                            dm.save(adBean);//将实体类缓存
                        }
//                        dm.close();//关闭数据库连接
                        viewpager.setAdapter(new ADAdapter(act, adBeens));
                        viewpager.setLooperPic(true);//是否设置自动轮播
                        indicator.setViewPager(viewpager);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } /*catch (DbException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }
}
