package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.widgets.RulerView;

/**
 * Created by chenming on 2018/8/28
 */
public class RulerActivity extends BaseActivity {
    private TextView mTvValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler_view);
        RulerView rulerView = findViewById(R.id.tape);
        mTvValue = findViewById(R.id.tv_value);
        mTvValue.setText(rulerView.getValue() + " 厘米");
        rulerView.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onChange(float value) {
                mTvValue.setText(value + " 厘米");
            }
        });
    }
}
