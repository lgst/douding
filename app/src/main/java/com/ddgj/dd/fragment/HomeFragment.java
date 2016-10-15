package com.ddgj.dd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddgj.dd.R;
import com.ddgj.dd.activity.SearchActivity;
import com.ddgj.dd.adapter.HomeRecyclerViewAdapter;
import com.ddgj.dd.fragment.dummy.HomeListContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class HomeFragment extends BaseFragment {
    private SwipeRefreshLayout refreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        HomeListContent.getInstance().init(getActivity());
        recyclerView.setAdapter(new HomeRecyclerViewAdapter(getActivity(), HomeListContent.ITEMS));
    }

}
