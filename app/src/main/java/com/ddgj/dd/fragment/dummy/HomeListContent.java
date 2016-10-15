package com.ddgj.dd.fragment.dummy;

import android.content.Context;

import com.ddgj.dd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * : Replace all uses of this class before publishing your app.
 */
public class HomeListContent {

    private Context context;
    private boolean isFirst = true;
    private static HomeListContent instance;

    public static final List<Object> ITEMS = new ArrayList<Object>();
    private AD ad;
    private Classes classes;

    private HomeListContent() {
    }

    public static final HomeListContent getInstance() {
        if (instance == null) {
            instance = new HomeListContent();
        }
        return instance;
    }

    private static void addItem(Object item) {
        ITEMS.add(item);
    }

    public void init(Context context) {
        if (isFirst) {
            this.context = context;
            initAD();
            initClasses();
            isFirst = false;
            return;
        }
    }

    private void initClasses() {
        classes = new Classes();
        ITEMS.add(classes);
    }

    private void initAD() {
        ad = new AD();
        ad.add(R.mipmap.banner1);
        ad.add(R.mipmap.banner2);
        ad.add(R.mipmap.banner3);
        ITEMS.add(ad);
    }

    /**
     * 轮播图，广告
     */
    public class AD {
        private final List<Integer> IMGS = new ArrayList<Integer>();

        public AD() {
        }

        public void add(int img) {
            IMGS.add(img);
        }

        public List<Integer> getIMGS() {
            return IMGS;
        }
    }

    /**
     * 分类
     */
    public class Classes {
        private final String[] names = new String[]{"个人创意", "专利服务", "私人订制", "委托代工", "全民众筹", "中国智造"};
        private final int[] IMGS = new int[]{
                R.mipmap.ic_home_classes_chuangyi,
                R.mipmap.ic_home_classes_zhuangli,
                R.mipmap.ic_home_classes_dingzhi,
                R.mipmap.ic_home_classes_daigong,
                R.mipmap.ic_home_classes_zhongchou,
                R.mipmap.ic_home_classes_gongchang};

        public final String[] getNames() {
            return names;
        }

        public final int[] getIMGS() {
            return IMGS;
        }
    }

    public class Title {
        private String title;
        private int icon;

        public Title() {
        }

        public Title(int icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}





