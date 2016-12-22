package com.ddgj.dd.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ddgj.dd.R;
import com.ddgj.dd.util.net.NetWorkInterface;
import com.ddgj.dd.view.PinchImageView;

/**
 * Created by Administrator on 2016/11/2.
 */

public class PhotoScale extends BaseActivity {

    private PinchImageView view;

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        view = (PinchImageView) findViewById(R.id.image);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final String photoURL = getIntent().getStringExtra("photoURL");
        SimpleTarget target2 = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                view.setImageBitmap(bitmap);
            }
        };
        Glide.with(this )
                .load(NetWorkInterface.HOST + "/" + photoURL )
                .asBitmap()
                .into(target2);
    }
}
