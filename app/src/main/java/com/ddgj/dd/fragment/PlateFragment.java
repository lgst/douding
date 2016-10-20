package com.ddgj.dd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddgj.dd.R;

/**
 * Created by Administrator on 2016/10/20.
 */

public class PlateFragment extends BaseFragment {
    @Override
    protected void initViews() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();

        return view;
    }

    private View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_plate, null);
        return view;
    }
}
