package com.kakaxicm.geekming.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakaxicm.geekming.R;

/**
 * Created by chenming on 2018/8/21
 * 一个子View控制另外一个子View
 */
public class DependencyBehavior extends CoordinatorLayout.Behavior<View> {
    private int mPreOffset;//记录上一次控制View的位置
    private int mCurOffset;
    public DependencyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency.getId() == R.id.depentent;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        mCurOffset = dependency.getTop();//当前控制View的位置
        int dy = mCurOffset - mPreOffset;//记录当前需要滚动的偏移
        dy = dy*2;//差分滚动
        mPreOffset = mCurOffset;//更新
        Log.e("onDependentViewChanged", dy+"");
        ViewCompat.offsetTopAndBottom(child, -dy);
        return true;
    }
}
