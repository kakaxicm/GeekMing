package com.kakaxicm.geekming.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.WaveBallView;

@ContentViewAnnotation(value = R.layout.activity_wave_ball)
public class WaveBallActivity extends BaseActivity {
    @ViewIdAnnotation(value = R.id.wave_view)
    private WaveBallView waveBallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim();
            }
        });
    }

    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(30000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                waveBallView.setProgress(value);
            }
        });
        animator.start();
    }
}
