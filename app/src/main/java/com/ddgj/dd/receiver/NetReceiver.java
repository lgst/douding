package com.ddgj.dd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.ddgj.dd.util.net.BusEvent;
import com.ddgj.dd.util.net.NetUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/12/8.
 */

public class NetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            boolean isConnected = NetUtils.isConnected(context);
            if (isConnected) {
//                Toast.makeText(context, "已经连接网络", Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(new BusEvent(BusEvent.NET_STATUS).setConnect(true));
            } else {
                EventBus.getDefault().post(new BusEvent(BusEvent.NET_STATUS).setConnect(false));
//                Toast.makeText(context, "已经断开网络", Toast.LENGTH_LONG).show();
            }
        }
    }

}
