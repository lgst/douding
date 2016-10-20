package com.ddgj.dd.activity;

import android.os.Bundle;

import com.ddgj.dd.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

/**
 * Created by Administrator on 2016/10/15.
 */
public class ChatActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    @Override
    public void initView() {
        //new出EaseChatFragment或其子类的实例
        EaseChatFragment chatFragment = new EaseChatFragment();
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        String userId = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        args.putString(EaseConstant.EXTRA_USER_ID, userId);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content_container,chatFragment).commit();
    }
}
