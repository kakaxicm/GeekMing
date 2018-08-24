package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.kakaxicm.geekming.R;

/**
 * Created by chenming on 2018/8/24
 */
public class StickyLayout extends LinearLayout {
    //处理滚动需要的参量
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mTouchSlop;
    //滚动帮助类
    private OverScroller mScroller;
    private View mTop;
    private View mNav;
    private ViewPager mViewPager;
    private int mTopViewHeight;
    private float mLastY;
    private boolean mDragging;
    //当前内部的消费事件的View
    private ViewGroup mInnerScrollView;
    private boolean isTopHidden;

    private boolean isInControl = false;

    public StickyLayout(Context context) {
        super(context, null);
    }

    public StickyLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
//        if (mVelocityTracker == null) {
//            mVelocityTracker = VelocityTracker.obtain();
//        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTop = findViewById(R.id.id_stickynavlayout_topview);
        mNav = findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = findViewById(R.id.id_stickynavlayout_viewpager);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量ViewPager的高度，ViewPager的高度需要父布局指定
        LinearLayout.LayoutParams params = (LayoutParams) mViewPager.getLayoutParams();
        params.height = getMeasuredHeight() - mNav.getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    /**
     * 处理下滑时，子View不能滑动的时候 拉出头部布局
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                getCurrentScrollView();
                //子滑动View无法继续向下滑动
                if (mInnerScrollView instanceof ScrollView) {
                    if (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0 && !isInControl) {
                        isInControl = true;//只执行一次的标记
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        //发送取消事件重置触摸状态
                        dispatchTouchEvent(ev);
                        //在发射Down事件
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        //继续调用本方法，重新事件分发,此时isInControl=true，不会再进到这段代码
                        return dispatchTouchEvent(ev2);
                    }
                } else if (mInnerScrollView instanceof ListView) {
                    ListView lv = (ListView) mInnerScrollView;
                    View c = lv.getChildAt(lv.getFirstVisiblePosition());
                    if (!isInControl && c != null && c.getTop() == 0 && isTopHidden
                            && dy > 0) {
                        isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        return dispatchTouchEvent(ev2);
                    }
                } else if (mInnerScrollView instanceof RecyclerView) {
                    RecyclerView rv = (RecyclerView) mInnerScrollView;
                    boolean canScrollDown = ViewCompat.canScrollVertically(rv, -1);
                    if (!isInControl && !canScrollDown && isTopHidden
                            && dy > 0) {
                        isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        return dispatchTouchEvent(ev2);
                    }
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * onTouchEvent处理这个layout的滚动,拦截后走这个方法
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        //加入速度检测
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //如果滚动没有完成，终止当前的滚动
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                //当前的滑动距离
                float dy = y - mLastY;
                //滚动条件
                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, (int) -dy);
                    // 如果topView隐藏，且上滑动时，则改变当前事件为ACTION_DOWN
                    if (getScrollY() == mTopViewHeight && dy < 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isInControl = false;
                    }

                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                //获取滑动速度
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    //TODO fling手势
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 关键代码手指抬起的fling动作实现
     *
     * @param velocityY
     */
    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }


    @Override
    public boolean onInterceptHoverEvent(MotionEvent ev) {
        //1.如果我们的顶部view没有完全隐藏，拦截事件；
        //2.顶部View彻底隐藏，切子View无法向下滑动时，拦截事件
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                getCurrentScrollView();
                if (Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                    if (mInnerScrollView instanceof ScrollView) {
                        // 如果topView没有隐藏
                        // 或sc的scrollY = 0 && topView隐藏 && 下拉，则拦截
                        if (!isTopHidden
                                || (mInnerScrollView.getScrollY() == 0
                                && isTopHidden && dy > 0)) {

                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    } else if (mInnerScrollView instanceof ListView) {

                        ListView lv = (ListView) mInnerScrollView;
                        View c = lv.getChildAt(lv.getFirstVisiblePosition());
                        // 如果topView没有隐藏
                        // 或sc的listView在顶部 && topView隐藏 && 下拉，则拦截

                        if (!isTopHidden || //
                                (c != null //
                                        && c.getTop() == 0//
                                        && isTopHidden && dy > 0)) {

                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    } else if (mInnerScrollView instanceof RecyclerView) {
                        RecyclerView rv = (RecyclerView) mInnerScrollView;
                        if (!isTopHidden || (!android.support.v4.view.ViewCompat.canScrollVertically(rv, -1) && isTopHidden && dy > 0)) {
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    }
//                    //条件1和2
//                    if (!isTopHidden || (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0)) {
//                        return true;
//                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
                recycleVelocityTracker();
                break;
        }

        return super.onInterceptHoverEvent(ev);
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }


    /**
     * 获取当前消费事件的内部子View
     */
    private void getCurrentScrollView() {

        int currentItem = mViewPager.getCurrentItem();
        PagerAdapter a = mViewPager.getAdapter();
        if (a instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
            Fragment item = (Fragment) fadapter.instantiateItem(mViewPager,
                    currentItem);
            mInnerScrollView = (item.getView()
                    .findViewById(R.id.id_stickynavlayout_innerscrollview));
        } else if (a instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
            Fragment item = (Fragment) fsAdapter.instantiateItem(mViewPager,
                    currentItem);
            mInnerScrollView = item.getView()
                    .findViewById(R.id.id_stickynavlayout_innerscrollview);
        }

    }


    //覆写这个方法限定滚动范围
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
        isTopHidden = getScrollY() == mTopViewHeight;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}
