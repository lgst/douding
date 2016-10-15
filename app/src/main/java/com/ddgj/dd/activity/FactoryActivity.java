package com.ddgj.dd.activity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ddgj.dd.R;
import com.ddgj.dd.adapter.FactoryAdapter;
import com.ddgj.dd.adapter.FactoryProductsAdapter;


/**
 * Created by Administrator on 2016/10/13/0013.
 */

public class FactoryActivity extends BaseActivity{

    private RadioGroup radioGroup;
    private RadioButton enterprise;//企业
    private RadioButton product;//产品
    private FactoryAdapter factoryAdapter;
    private FactoryProductsAdapter factoryProductsAdapter;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);

        initViews();

    }

    /**
     * 初始化控件
     */
    @Override
    public void initViews() {
        enterprise = (RadioButton)this.findViewById(R.id.rb_factory_enterprise);
        product = (RadioButton)this.findViewById(R.id.rb_factory_product);
        radioGroup = (RadioGroup)this.findViewById(R.id.rg_factory);
        radioGroup.check(R.id.rb_factory_enterprise);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    //企业
                    case R.id.rb_factory_enterprise :
                        break;
                    //产品
                    case R.id.rb_factory_product :
                        break;
                }
            }
        });

    }

}
