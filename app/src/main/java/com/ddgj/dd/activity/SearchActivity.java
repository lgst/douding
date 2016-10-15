package com.ddgj.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ddgj.dd.R;


public class SearchActivity extends BaseActivity implements View.OnClickListener{
    /**分类*/
    private TextView classes;
    /**类别名称*/
    private String[] popitme;

    private EditText searchContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
    }

    @Override
    public void initViews() {
        popitme = getResources().getStringArray(R.array.classes);
        searchContent = (EditText) findViewById(R.id.search_edit_text);

    }

    public void backClick(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void searchClick(View v)
    {
        String content = searchContent.getText().toString().trim();
        if(content.isEmpty())
        {
            showToastShort("请输入关键字");
            searchContent.requestFocus();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("content",content);
        setResult(SUCCESS,intent);
        finish();
    }
}
