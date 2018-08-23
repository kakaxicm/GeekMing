package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.activity.BaseActivity;

/**
 * Created by chenming on 2018/8/21
 * Behavior案例1：一个view的状态依赖另一个view
 */
public class Behavior1Activity extends BaseActivity {
    private int curY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior1);

        final View depentent = findViewById(R.id.depentent);
        depentent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        curY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        float dy = y - curY;
                        Log.e("onTouch", v.getTop()+"");

                        ViewCompat.offsetTopAndBottom(v, (int) dy);
                        break;
                }
                return true;
            }
        });

    }
}
