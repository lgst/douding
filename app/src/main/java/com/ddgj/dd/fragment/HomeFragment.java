package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.activity.WebActivity;
import com.ddgj.dd.adapter.ADAdapter;
import com.ddgj.dd.adapter.ClassesGridViewAdapter;
import com.ddgj.dd.adapter.OriginalityPLVAdapter;
import com.ddgj.dd.adapter.PatentPLVAdapter;
import com.ddgj.dd.bean.ADBean;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.bean.ResponseInfo;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
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

import static com.hyphenate.chat.EMGCMListenerService.TAG;


/**
 * 主页
 */
public class HomeFragment extends BaseFragment implements NetWorkInterface {
    private BaseActivity act;
    private LoopViewPager viewpager;
    private CircleIndicator indicator;
    private CustomGridView classesGV;
    private CustomListView patentListView;
    private CustomListView originalityListView;
    private List<Originality> mOriginalitys;
    private List<Patent> mPatents;

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
        initView();
        initCacha();//加载缓存
        initAD();//从网络加载轮播图
        initPatent();//从网络加载专利
        initOriginality();//从网络加载创意
    }

    private void initCacha() {
        /*加载缓存广告*/
        analysisAndLoadAD(FileUtil.readJsonFromCacha("ad"));
        /**加载缓存创意*/
        analysisAndLoadOriginality(FileUtil.readJsonFromCacha("originality"));
        /**加载缓存专利*/
        analysisAndLoadPatent(FileUtil.readJsonFromCacha("patent"));
    }

    /**
     * 初始化创意列表
     */
    private void initOriginality() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "3");
        params.put("originality_differentiate", "0");
        new HttpHelper<Originality>(getActivity(),Originality.class,true)
        .getDatasPost(GET_HOT_ORIGINALITY, params, new DataCallback<Originality>() {
            @Override
            public void Failed(Exception e) {
                Log.e(TAG, "Failed: "+e.getMessage() );
            }

            @Override
            public void Success(List<Originality> datas) {
                mOriginalitys=datas;
                originalityListView.setAdapter(new OriginalityPLVAdapter(act, datas));
            }
        });
//        OkHttpUtils.post().url(GET_HOT_ORIGINALITY).params(params).build().execute(
//                new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        act.showToastNotNetWork();
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        Log.i("lgst", response);
//                        analysisAndLoadOriginality(response);
//                        FileUtil.saveJsonToCacha(response, "originality");
//                    }
//                }
//        );
        originalityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Originality originality = mOriginalitys.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("originality_id", originality.getOriginality_id());
                OkHttpUtils.post().url(GET_ORIGINALITY_DETAILS).params(params).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lgst", "获取创意详情页失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                        if (responseInfo.getStatus() == STATUS_SUCCESS) {
                            String url = responseInfo.getData();
                            Log.e("lgst", url);
                            startActivity(new Intent(getActivity(), WebActivity.class)
                                    .putExtra("title", originality.getOriginality_name())
                                    .putExtra("url", HOST + url)
                                    .putExtra("account", originality.getAccount())
                                    .putExtra("content", originality.getOriginality_details())
                                    .putExtra("id",originality.getOriginality_id())
                                    .putExtra("classes", 0));
                        }
                    }
                });
            }
        });
    }

    /**
     * 解析创意json数据
     */
    private void analysisAndLoadOriginality(String response) {
        if (response == null) {
            return;
        }
        try {
            JSONObject jo = new JSONObject(response);
            int status = jo.getInt("status");
            if (status == STATUS_SUCCESS) {
                mOriginalitys = new ArrayList<Originality>();
                JSONArray ja = jo.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++) {
                    String patentStr = ja.getJSONObject(i).toString();
                    Originality originality = new Gson().fromJson(patentStr, Originality.class);
                    mOriginalitys.add(originality);
                }
                originalityListView.setAdapter(new OriginalityPLVAdapter(act, mOriginalitys));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化专利列表
     */
    private void initPatent() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "3");
        OkHttpUtils.post().url(GET_HOT_PATENT).params(params).build().execute(
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("lgst", "首页专利加载出错：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("lgst", response);
                        analysisAndLoadPatent(response);
//                写入json缓存
                        FileUtil.saveJsonToCacha(response, "patent");
                    }
                }

        );
        patentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Patent originality = mPatents.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("patent_id", originality.getPatent_id());
                OkHttpUtils.post().url(GET_PATENT_DETAILS).params(params).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lgst", "获取专利详情页失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResponseInfo responseInfo = new Gson().fromJson(response, ResponseInfo.class);
                        if (responseInfo.getStatus() == STATUS_SUCCESS) {
                            String url = responseInfo.getData();
                            Log.e("lgst", url);
                            startActivity(new Intent(getActivity(), WebActivity.class)
                                    .putExtra("title", originality.getPatent_name())
                                    .putExtra("url", HOST + url)
                                    .putExtra("account", originality.getAccount()).putExtra("content", originality.getPatent_details()));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
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
        Map<String,String> params = new HashMap<String,String>();
        params.put("login", UserHelper.getInstance().isLogined()?"0":"1");
        OkHttpUtils.post().url(NetWorkInterface.GET_AD).params(params).id(100).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i("lgst", "获取轮播图失败：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                analysisAndLoadAD(response);
                FileUtil.saveJsonToCacha(response, "ad");
            }
        });
    }

    /**
     * 解析轮播图json数据
     */
    private void analysisAndLoadAD(String response) {
        if (response == null) {
            return;
        }
        try {
            JSONObject jo = new JSONObject(response);
            if (STATUS_SUCCESS == jo.getInt("status")) {
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
                }
                viewpager.setAdapter(new ADAdapter(act, adBeens));
                viewpager.setLooperPic(true);//是否设置自动轮播
                indicator.setViewPager(viewpager);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析专利json数据
     */
    public void analysisAndLoadPatent(String response) {
        if (response == null) {
            return;
        }
        try {
            mPatents = new ArrayList<Patent>();
            JSONObject jo = new JSONObject(response);
            int status = jo.getInt("status");
            if (status == STATUS_SUCCESS) {
                JSONArray ja = jo.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++) {
                    String patentStr = ja.getJSONObject(i).toString();
                    Patent patent = new Gson().fromJson(patentStr, Patent.class);
                    mPatents.add(patent);
                }
                patentListView.setAdapter(new PatentPLVAdapter(act, mPatents));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
