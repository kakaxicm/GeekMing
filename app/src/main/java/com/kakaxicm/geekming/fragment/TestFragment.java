package com.kakaxicm.geekming.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.rcvadapter.UniversalRcvAdapter;
import com.kakaxicm.geekming.frameworks.rcvadapter.UniversalRcvViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by chenming on 2018/8/22
 */
public class TestFragment extends Fragment{
    private View mRootView;
    private RecyclerView mRcv;

    List<String> mData = new ArrayList<>();
    UniversalRcvAdapter<String> mAdapter = new UniversalRcvAdapter<>(mData, new UniversalRcvAdapter.OnBindDataInterface<String>() {
        @Override
        public void onBindData(String model, UniversalRcvViewHolder holder, int pos, int type) {
            holder.setText(R.id.tv1, model);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item1_conmon_list;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_test, container, false);
        mRcv = mRootView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        for (int i = 0; i < 100; i++) {
            mData.add(i + "");
        }

        mRcv.setLayoutManager(new LinearLayoutManager(container.getContext(), VERTICAL, false));
        mRcv.setAdapter(mAdapter);
        return mRootView;
    }
}
