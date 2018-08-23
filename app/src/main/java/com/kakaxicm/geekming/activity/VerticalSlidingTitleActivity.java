package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.activity.BaseActivity;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.VerticalSlidingTitleLayout;

/**
 * Created by star on 15/12/30.
 */
@ContentViewAnnotation(value = R.layout.activity_vertical_sliding_title)
public class VerticalSlidingTitleActivity extends BaseActivity {
    @ViewIdAnnotation(value = R.id.id_sliding_title)
    private VerticalSlidingTitleLayout mTitleLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleLayout.setOnToggleListener(new VerticalSlidingTitleLayout.OnToggleListener() {
            @Override
            public void onToggleOn() {
                Log.e("TouchY", "ToggleOn");
            }

            @Override
            public void onToggleOff() {
                Log.e("TouchY", "ToggleOff");
            }

            @Override
            public void onSliding(float percent) {
                Log.e("TouchY", "Sliding" + percent);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }
}
