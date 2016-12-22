package com.ddgj.dd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddgj.dd.R;
import com.ddgj.dd.util.DensityUtil;
import com.ddgj.dd.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    private EditText searchContent;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<String> mHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        loadHistory();
        initView();
    }

    @Override
    public void initView() {
        searchContent = (EditText) findViewById(R.id.search_edit_text);
        searchContent.setOnEditorActionListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dip2px(getApplicationContext(), 8), Color.parseColor("#EEEEEE")));
        mRecyclerView.setAdapter(new RcvAdapter());
    }

    private void saveHistory(String str) {
        if (mHistory.contains(str)) {
            mHistory.remove(str);
        }
        mHistory.add(0, str);
        StringBuilder keySb = new StringBuilder();
        for (String keyWords : mHistory) {
            keySb.append(keyWords).append(" ");
        }
        SharedPreferences sp = getSharedPreferences("search", MODE_APPEND);
        sp.edit().putString("search", keySb.toString().trim()).commit();
    }

    private void saveHistory() {
        StringBuilder keySb = new StringBuilder();
        for (String keyWords : mHistory) {
            keySb.append(keyWords).append(" ");
        }
        SharedPreferences sp = getSharedPreferences("search", MODE_APPEND);
        sp.edit().putString("search", keySb.toString().trim()).commit();
    }

    private void loadHistory() {
        SharedPreferences sp = getSharedPreferences("search", MODE_APPEND);
        String keyWords = sp.getString("search", "");
        if (!keyWords.isEmpty())
            mHistory.addAll(Arrays.asList(keyWords.split(" ")));
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH && submit()) {
            String content = searchContent.getText().toString().trim();
            if (content.isEmpty()) {
                showToastShort("请输入关键字");
                searchContent.requestFocus();
                return false;
            }
            saveHistory(content);
            Intent intent = new Intent();
            intent.putExtra("content", content);
            setResult(SUCCESS, intent);
            finish();
        }
        return false;
    }
    private boolean submit() {
        // validate
        String searchString = searchContent.getText().toString().trim();
        if (TextUtils.isEmpty(searchString)) {
            Toast.makeText(this, "输入关键字", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    class RcvAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_history_list, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder vh = (ViewHolder) holder;
            vh.mText.setText(mHistory.get(position));
            vh.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveHistory(mHistory.get(position));
                    Intent intent = new Intent();
                    intent.putExtra("content", mHistory.get(position));
                    setResult(SUCCESS, intent);
                    SearchActivity.this.finish();
                }
            });
            vh.mDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHistory.remove(position);
                    notifyItemRemoved(position);
                    RcvAdapter.this.notifyItemRangeChanged(0, mHistory.size());
                    saveHistory();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mHistory.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View rootView;
            AppCompatTextView mText;
            ImageView mDel;

            ViewHolder(View rootView) {
                super(rootView);
                this.rootView = rootView;
                this.mText = (AppCompatTextView) rootView.findViewById(R.id.text);
                this.mDel = (ImageView) rootView.findViewById(R.id.delete);
            }

        }
    }
}
