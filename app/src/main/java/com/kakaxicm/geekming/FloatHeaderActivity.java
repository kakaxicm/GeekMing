package com.kakaxicm.geekming;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kakaxicm.geekming.frameworks.rcvadapter.UniversalRcvAdapter;
import com.kakaxicm.geekming.frameworks.rcvadapter.UniversalRcvViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by chenming on 2018/8/22
 */
public class FloatHeaderActivity extends BaseActivity {
    RecyclerView mRcv;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_head);

        for (int i = 0; i < 100; i++) {
            mData.add(i + "");
        }

        mRcv = findViewById(R.id.rcv);
        mRcv.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
        mRcv.setAdapter(mAdapter);
    }
}
