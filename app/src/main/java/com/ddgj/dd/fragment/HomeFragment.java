package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.BaseActivity;
import com.ddgj.dd.adapter.ADAdapter;
import com.ddgj.dd.adapter.ClassesGridViewAdapter;
import com.ddgj.dd.adapter.OriginalityPLVAdapter;
import com.ddgj.dd.adapter.PatentPLVAdapter;
import com.ddgj.dd.bean.ADBean;
import com.ddgj.dd.bean.Originality;
import com.ddgj.dd.bean.Patent;
import com.ddgj.dd.util.FileUtil;
import com.ddgj.dd.util.net.DataCallback;
import com.ddgj.dd.util.net.HttpHelper;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.util.user.UserHelper;
import com.ddgj.dd.view.CustomGridView;
import com.ddgj.dd.view.CustomListView;
import com.hejunlin.superindicatorlibray.CircleIndicator;
import com.hejunlin.superindicatorlibray.LoopViewPager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private HttpHelper<ADBean> adHttphelper;
    private HttpHelper<Originality> oriHttpHelper;
    private HttpHelper<Patent> patentHttpHelper;

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
        adHttphelper = new HttpHelper<ADBean>(getActivity(), ADBean.class, true);
        String adData = FileUtil.readJsonFromCache(ADBean.class.getName());
        if (adData != null) {
            List<ADBean> adBeens = adHttphelper.analysisAndLoadOriginality(adData);
            viewpager.setAdapter(new ADAdapter(act, adBeens));
            viewpager.setLooperPic(true);//是否设置自动轮播
            indicator.setViewPager(viewpager);
        }
        /*
        *加载缓存创意
        */
        oriHttpHelper = new HttpHelper<Originality>(getActivity(), Originality.class, true);
        //获取缓存的JSON数据
        String oriData = FileUtil.readJsonFromCache(Originality.class.getName());
        //根据JSON数据获取数据集合
        mOriginalitys = oriHttpHelper.analysisAndLoadOriginality(oriData);
        originalityListView.setAdapter(new OriginalityPLVAdapter(act, mOriginalitys));

        /*加载缓存专利*/
        patentHttpHelper = new HttpHelper<Patent>(getActivity(), Patent.class, true);
        //JSON数据
        String patentData = FileUtil.readJsonFromCache(Patent.class.getName());
        //解析JSON数据，返回集合
        mPatents = patentHttpHelper.analysisAndLoadOriginality(patentData);
        patentListView.setAdapter(new PatentPLVAdapter(getActivity(), mPatents));
    }

    /**
     * 初始化创意列表
     */
    private void initOriginality() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "3");
        params.put("originality_differentiate", "0");
        oriHttpHelper.getDatasPost(GET_HOT_ORIGINALITY, params, new DataCallback<Originality>() {
            @Override
            public void Failed(Exception e) {
                Log.e(TAG, "Failed: " + e.getMessage());
            }

            @Override
            public void Success(List<Originality> datas) {
                mOriginalitys = datas;
                originalityListView.setAdapter(new OriginalityPLVAdapter(act, datas));
            }
        });
        originalityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Originality originality = mOriginalitys.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("originality_id", originality.getOriginality_id());
                oriHttpHelper.startDetailsPage(GET_ORIGINALITY_DETAILS, params, originality);
            }
        });
    }

    /**
     * 初始化专利列表
     */
    private void initPatent() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNumber", "1");
        params.put("pageSingle", "3");
        patentHttpHelper.getDatasPost(GET_HOT_PATENT, params, new DataCallback<Patent>() {
            @Override
            public void Failed(Exception e) {
                Log.i("lgst", "首页专利加载出错：" + e.getMessage());
            }

            @Override
            public void Success(List<Patent> datas) {
                mPatents = datas;
                patentListView.setAdapter(new PatentPLVAdapter(act, mPatents));
            }
        });
        patentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Patent patent = mPatents.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_side", "app");
                params.put("patent_id", patent.getPatent_id());
                patentHttpHelper.startDetailsPage(GET_PATENT_DETAILS, params, patent);
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
        Map<String, String> params = new HashMap<String, String>();
        params.put("login", UserHelper.getInstance().isLogined() ? "0" : "1");
        adHttphelper.getDatasPost(GET_AD, params, new DataCallback<ADBean>() {
            @Override
            public void Failed(Exception e) {
                Log.e("lgst", "获取轮播图失败：" + e.getMessage());
            }

            @Override
            public void Success(List<ADBean> datas) {
                viewpager.setAdapter(new ADAdapter(act, datas));
                viewpager.setLooperPic(true);//是否设置自动轮播
                indicator.setViewPager(viewpager);
            }
        });
    }
}
