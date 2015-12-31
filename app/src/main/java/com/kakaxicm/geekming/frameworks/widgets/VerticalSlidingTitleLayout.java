package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.kakaxicm.geekming.R;

/**
 * Created by star on 15/12/30.
 * scroller的基本用法+可变的path
 */
public class VerticalSlidingTitleLayout extends LinearLayout {
    private View mTopView;
    private int mTopheight;
    private float mLastY;
    private VerticalArrowView mVerticalArrowView;
    //滚动相关
    private Scroller mScroller;


    private boolean mIsToggleOn;

    private OnToggleListener mToggleListener;//对外的滑动监听

    public VerticalSlidingTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        mScroller =  new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = findViewById(R.id.id_sliding_title_head);
        mVerticalArrowView = (VerticalArrowView) findViewById(R.id.id_vertical_arrow);
        findViewById(R.id.id_vertical_arrow).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsToggleOn) {
                    mScroller.startScroll(0, 0, 0, mTopheight, 1000);
                }else {
                    mScroller.startScroll(0, getScrollY(), 0, -mTopheight, 1000); //向下滑动
                }
                invalidate();
            }
        });

    }

    public void setOnToggleListener(OnToggleListener listener) {
        mToggleListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTopheight = mTopView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //hide top first
        scrollTo(0, mTopheight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = (int) y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float delY = y - mLastY;
                scrollBy(0, (int) -delY);
                mLastY = y;
                /**
                 * 设定滑动范围
                 */
                if(getScrollY() >= mTopheight || getScrollY() <= 0) {
                    return super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                //弹性滚动到边界
                int scrollY = getScrollY();
                int delScrollY = 0;
                if(scrollY > 0 && scrollY < mTopheight/2) {
                    delScrollY = scrollY - getMeasuredHeight();//
                }else if (scrollY > mTopheight/2 && scrollY < mTopheight){
                    delScrollY = mTopheight - getScrollY();
                }
                mScroller.startScroll(0, scrollY, 0, delScrollY, 1000);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void scrollTo(int x, int y) {
        if(y < 0) {
            y = 0;
        }
        if(y > mTopheight) {
            y = mTopheight;
        }

        if(y != getScrollY()) {
            super.scrollTo(x, y);
        }

        if(y>0 && y<mTopheight) {
            float percent = 1.0f * y / mTopheight;
            mVerticalArrowView.onScrollPercent(percent);
            if(mToggleListener != null) {
                mToggleListener.onSliding(percent);
            }
        }

        if(y <= 0) {
            mIsToggleOn = true;
            if(mToggleListener != null) {
                mToggleListener.onToggleOn();
            }
        }else if(y >= mTopheight){
            mIsToggleOn = false;
            if(mToggleListener != null) {
                mToggleListener.onToggleOff();
            }
        }



    }

    public interface OnToggleListener {
        void onToggleOn();
        void onToggleOff();
        void onSliding(float percent);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}
