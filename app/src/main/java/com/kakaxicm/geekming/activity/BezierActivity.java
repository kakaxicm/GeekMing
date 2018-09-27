package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.widgets.SpringIndicator;

/**
 * Created by chenming on 2018/8/31
 */
public class BezierActivity extends BaseActivity {
    private SpringIndicator springIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ios_bezier);
        springIndicator = findViewById(R.id.spring_indicator);
    }

    public void start(View view){
        springIndicator.start();
    }
}
