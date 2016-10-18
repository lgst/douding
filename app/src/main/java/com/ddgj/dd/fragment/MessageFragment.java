package com.ddgj.dd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ddgj.dd.R;

import java.util.List;


/**
 * Created by Administrator on 2016/9/29.
 */
public class MessageFragment extends BaseFragment {
    private FrameLayout mFrameLayout;
   /* private EMMessageListener msgListener;
    private EaseConversationListFragment conversationListFragment;
*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    protected void initViews() {

     /*   msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                conversationListFragment.refresh();
                Log.i("lgst","收到消息");
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
                Log.i("lgst","收到透传消息");
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
                Log.i("lgst","收到已读回执");
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
                Log.i("lgst","收到已送达回执");
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
                Log.i("lgst","消息状态变动");
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        conversationListFragment = new EaseConversationListFragment();
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }
        });
        mFrameLayout = (FrameLayout) findViewById(R.id.content_container);
        getFragmentManager().beginTransaction().add(R.id.content_container, conversationListFragment).commit();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}
