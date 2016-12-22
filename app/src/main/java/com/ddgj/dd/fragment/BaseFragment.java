package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/9/28.
 */
public abstract class BaseFragment extends Fragment {
    public static final int SUCCESS = 100;
    public static final int FAILDE = 101;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View findViewById(int id){
        return getView().findViewById(id);
    }

    protected abstract void initView();

    protected void logi(String msg){
        Log.i("lgst", msg);
    }
    protected void loge(String msg){
        Log.e("lgst", msg);
    }
}
