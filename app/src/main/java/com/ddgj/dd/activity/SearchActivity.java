package com.ddgj.dd.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ddgj.dd.R;


/**
 * 搜索界面
 * 传入参数：title：标题
 * 返回：content：搜索内容
 */
public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener {
    /**
     * 分类
     */
    private TextView classes;
    private ListView searchHistory;
    private EditText searchContent;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    @Override
    public void initView() {
        searchContent = (EditText) findViewById(R.id.search_edit_text);
        searchContent.setOnEditorActionListener(this);
        searchHistory = (ListView) findViewById(R.id.list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        final String sh = sp.getString("search", "");
        if (sh.isEmpty())
            return;
        final String[] shs = sh.split(",");
        searchHistory.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return shs.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, null);
                TextView tv = (TextView) convertView.findViewById(R.id.text);
                Log.i("lgst", "p:" + position);
                tv.setText(shs[position]);
                return convertView;
            }
        });
        searchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("content", shs[position]);
                setResult(SUCCESS, intent);
                SearchActivity.this.finish();
            }
        });
    }

    public void backClick(View v) {
        finish();
    }

    /**
     * 清除搜索历史记录
     */
    public void clearClick(View v) {
        getSharedPreferences("search_history", MODE_PRIVATE).edit().clear().commit();
        searchHistory.setVisibility(View.INVISIBLE);
    }

    private void startSearch() {
        String content = searchContent.getText().toString().trim();
        if (content.isEmpty()) {
            showToastShort("请输入关键字");
            searchContent.requestFocus();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("content", content);
        setResult(SUCCESS, intent);
        saveToHistory(content);
        finish();
    }

    private void saveToHistory(String content) {
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        String searchHistory = sp.getString("search", "");
        if (searchHistory.isEmpty()) {
            sp.edit().putString("search", content).commit();
            return;
        }
        String[] shs = searchHistory.split(",");
        if (shs.length >= 10) {
            searchHistory = searchHistory.substring(0, searchHistory.lastIndexOf(','));
        }
        searchHistory = content + "," + searchHistory;
        sp.edit().putString("search", searchHistory).commit();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (searchContent.length() != 0)
                // 当按了搜索之后关闭软键盘
                ((InputMethodManager) searchContent.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            startSearch();
            return true;
        }
        return false;
    }
}
