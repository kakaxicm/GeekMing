package com.kakaxicm.geekming.utils;

import android.widget.AbsListView;

/**
 * Created by superwz on 14/12/1.
 */
public class ScrollListenerImpl implements AbsListView.OnScrollListener {
    /**
     * 我们的本意是通过onScrollStateChanged获知:每次GridView停止滑动时加载图片
     * 但是存在一个特殊情况:
     * 当第一次入应用的时候,此时并没有滑动屏幕的操作即不会调用onScrollStateChanged,但应该加载图片.
     * 所以在此处做一个特殊的处理.
     * 即代码:
     * if (isFirstEnterThisActivity && visibleItemCount > 0) {
     * loadBitmaps(firstVisibleItem, visibleItemCount);
     * isFirstEnterThisActivity = false;
     * }
     * <p>
     * ------------------------------------------------------------
     * <p>
     * 其余的都是正常情况.
     * 所以我们需要不断保存:firstVisibleItem和visibleItemCount
     * 从而便于中在onScrollStateChanged()判断当停止滑动时加载图片
     */
    //GridView中可见的第一张图片的下标
    private int mFirstVisibleItem = 0;
    //GridView中可见的图片的数量
    private int mVisibleItemCount = 0;

    //记录是否是第一次进入该界面
//    private boolean isFirstEnterThisActivity = true;

    private IScrollListenerImplCallBack mCallBack;

    public ScrollListenerImpl(IScrollListenerImplCallBack callBack) {
        mCallBack = callBack;

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
//        if (isFirstEnterThisActivity && visibleItemCount > 0 && mCallBack != null) {
////            mCallBack.loadBitmaps(firstVisibleItem, visibleItemCount);
//            isFirstEnterThisActivity = false;
//        }
    }

    /**
     * GridView停止滑动时下载图片
     * 其余情况下取消所有正在下载或者等待下载的任务
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCallBack.setScrollState(scrollState);
        if (scrollState == SCROLL_STATE_IDLE && mCallBack != null) {
            mCallBack.loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
            System.gc();
        } else {
//                cancelAllTasks();
        }
    }

    public interface IScrollListenerImplCallBack {
        /**
         * //     * 为GridView的item加载图片
         * //     *
         * //     * @param firstVisibleItem GridView中可见的第一张图片的下标
         * //     * @param visibleItemCount GridView中可见的图片的数量
         * //
         */
        void loadBitmaps(int firstVisibleItem, int visibleItemCount);

        void setScrollState(int scrollState);
    }
}