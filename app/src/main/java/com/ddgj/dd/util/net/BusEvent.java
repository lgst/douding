package com.ddgj.dd.util.net;

/**
 * Created by Administrator on 2016/12/8.
 */

public class BusEvent {
    public static final int LOGIN = 1;
    public static final int NET_STATUS = 2;
    public static final int ORI = 3;
    public static final int PATENT = 4;
    public static final int ORDER = 5;
    public static final int OEM = 6;
    public static final int ORDERS = 7;
    public static final int REWARD = 8;
    public static final int TENDER = 9;
    public static final int FAVORIT = 10;
    public static final int REWARD_ORDER = 11;
    public int what;

    public boolean isConnect;

    public BusEvent(int what) {
        this.what = what;
    }

    public BusEvent setConnect(boolean connect) {
        isConnect = connect;
        return this;
    }
}
