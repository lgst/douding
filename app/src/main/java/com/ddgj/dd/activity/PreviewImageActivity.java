package com.ddgj.dd.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.ddgj.dd.R;
import com.ddgj.dd.view.PinchImageView;

import java.util.List;

public class PreviewImageActivity extends Activity {
    private List<String> mImages;
    private ViewPager mVpImage;
    private AppCompatTextView mTvPageNumber;
    public static final String PARAMAS_POSITION = "position";
    public static final String PARAMAS_IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        init();
        initView();
    }

    private void init() {
        mImages = getIntent().getStringArrayListExtra(PARAMAS_IMAGES);
    }

    private void initView() {
        mVpImage = (ViewPager) findViewById(R.id.vp_image);
        mTvPageNumber = (AppCompatTextView) findViewById(R.id.tv_page_number);
        mTvPageNumber.setText("1/"+mImages.size());
        mVpImage.setOffscreenPageLimit(10);
        mVpImage.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PinchImageView piv = new PinchImageView(PreviewImageActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                piv.setLayoutParams(params);
                Glide.with(PreviewImageActivity.this)
                        .load(mImages.get(position))
                        .into(piv);
                piv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                container.addView(piv);
                return piv;
            }
        });
        mVpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvPageNumber.setText(position+1+"/"+mImages.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mVpImage.setCurrentItem(getIntent().getIntExtra(PARAMAS_POSITION,0));
    }
}
