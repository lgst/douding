package com.ddgj.dd.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ddgj.dd.R;

/**
 * Created by Administrator on 2016/10/17.
 */

public class PublishPatentActivity extends BaseActivity {

    private Spinner spinnerPatentType;
    private String sPatentTyoeSpinner;

    @Override
    public void initViews() {
        spinnerPatentType = (Spinner) findViewById(R.id.patent_type);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_patent);
        initViews();
        initTypeSpinner();

    }
    private void initTypeSpinner() {
        String[] mItems = getResources().getStringArray(R.array.patent_type);
        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,R.layout.textview_spinner_item,mItems);
        spinnerPatentType.setAdapter(spinnerAdapter);
        spinnerPatentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sPatentTyoeSpinner = String.valueOf(position);

                //Toast.makeText(PublishCreativeActivity.this, "你点击的是:"+position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
