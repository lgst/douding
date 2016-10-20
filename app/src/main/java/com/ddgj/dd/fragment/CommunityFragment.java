package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddgj.dd.R;


/**
 * Created by Administrator on 2016/9/29.
 */
public class CommunityFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community,container,false);
    }

    @Override
    protected void initView() {

    }
}
