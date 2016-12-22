package com.ddgj.dd.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.bean.ADBean;
import com.ddgj.dd.util.net.NetWorkInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LGST on 2016/12/4.
 * 轮播图控制
 */

public class Banner {
    private Context mContext;
    /**
     * 目标ViewPager
     */
    private ViewPager mVP;
    /**
     * 广告实体类
     */
    private List<ADBean> mBeans;
    /**
     * 轮播页面
     */
    private List<ImageView> viewList = new ArrayList<>();
    /**
     * 点击监听器
     */
    private OnItemClickListener onItemClickListener;
    /**
     * 计时器
     */
    private Timer mTimer;
    /**
     * viewpager当前显示的索引位置
     */
    private int pos = 0;//Integer.MAX_VALUE / 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mVP.setCurrentItem(++pos);
        }
    };
    /**
     * 暂停轮播图
     */
    private boolean pause;
    /**
     * 轮播中状态
     */
    private boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }


    public Banner(Context context) {
        this.mContext = context;
    }

    public Banner init(ViewPager mVP, List<ADBean> mBeans) {
        this.mVP = mVP;
        this.mBeans = mBeans;
        //装填ImageView
        for (int i = 0; i < mBeans.size(); i++) {
            ImageView iv = new ImageView(mContext);
            //在ImageView上显示图片
            Glide.with(mContext).load(NetWorkInterface.HOST + "/" + mBeans.get(i).getPicture())
                    .placeholder(R.mipmap.ic_ad_loading_bg)
                    .error(R.mipmap.ic_ad_loading_bg).into(iv);
            final int finalI = i;
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            //设置图片点击事件
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.OnItemClickListener(finalI);
                }
            });
            viewList.add(iv);
        }
        this.mVP.setAdapter(new VpAdapter());
        this.mVP.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING)//手动滑动重置计时器
                {
                    mTimer.cancel();
                    initTimer();
                }
            }
        });
        this.mVP.setCurrentItem(pos);
        return this;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * 设置监听器，当点击某张图时调用
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isPause() {
        return pause;
    }

    /**
     * 开始轮播
     */
    public Banner startBanner() {
        if (isRunning)
            stopBanner();
        isRunning = true;
        initTimer();
        return this;
    }

    /**
     * 初始化计时器
     */
    private void initTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
            }
        }, 3000, 3000);
    }

    /**
     * 停止轮播图
     */
    public void stopBanner() {
        isRunning = false;
        mTimer.cancel();
//        mVP = null;
//        mBeans = null;
    }

    /**
     * 暂停
     */
    public void pauseBanner() {
        pause = true;
        isRunning = false;
        mTimer.cancel();
    }

    /**
     * 继续
     */
    public void continueBanner() {
        pause = false;
        isRunning = true;
        //重新设置计时器
        initTimer();
    }

    public interface OnItemClickListener {
        /**
         * 当图片被点击时调用
         *
         * @param position 被点击图片对应的实体类的索引位置
         */
        void OnItemClickListener(int position);
    }

    /**
     * 适配器
     */
    class VpAdapter extends PagerAdapter {
        List<ImageView> views = new ArrayList<>();

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求模取出View列表中要显示的项
            position %= viewList.size();
            if (position < 0) {
                position = viewList.size() + position;
            }
            ImageView view = viewList.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            //add listeners here if necessary
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //不写任何操作，在instantItem方法中remove！！
        }
    }

//    private void logi(String msg) {
//        Log.i("lgst", msg);
//    }
}
