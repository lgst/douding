package com.ddgj.dd.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ddgj.dd.R;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public void initViews() {
        Log.i("d传到devo","d");

    }

    public void backClick(View v)
    {
        finish();
    }
}
