package com.kakaxicm.geekming.barrage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.kakaxicm.geekming.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Created by chenming on 2018/8/30
 */
public class BarrageView extends ViewGroup {

    // 移动速度
    public static final int LOWER_SPEED = 1;
    public static final int NORMAL_SPEED = 4;
    public static final int HIGH_SPEED = 8;

    // 弹幕出现的垂直位置支持
    public final static int GRAVITY_TOP = 1;    //001 只出现在屏幕顶部
    public final static int GRAVITY_CENTER = 2;  //010 只出现在屏幕中间
    public final static int GRAVITY_BOTTOM = 4;  //100 只出现在屏幕底部
    public final static int GRAVITY_FULL = 7;   //111  可以出现在任何位置

    private int mGravity = GRAVITY_FULL;

    private int mSpeed = 4;

    private int mSpanCount = 6;

    private int WIDTH, HEIGHT;

    private int mSingleLineHeight;

    private BarrageAdapter mAdapter;

    private List<View> mSpanList;//每一行新加进来的view,用于计算最大剩余空间的行
    //每一行等待发射的View
    private HashMap<Integer, LinkedList<View>> mWaitingViewsForLine;

    private OnItemClickListener onItemClickListener;

    private Boolean mIsQuit = false;
    //UI操作层 - 执行移动子View的handle
    @SuppressLint("HandlerLeak")
    Handler mMainMoveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                for (int i = 0; i < BarrageView.this.getChildCount(); i++) {
                    View view = BarrageView.this.getChildAt(i);
                    //view没有完全离开左边屏幕
                    if (!isViewLeaveLeftBound(view)) {

                        if (!isInWaitingQueue(view)) {//如果View没有在等待队列,则滑动它
                            float x = view.getX();
                            int width = view.getWidth();
                            Log.e("ScrollParams", "x = " + x + ", width = " + width + ", right = " + getRight());
                            // 向左滑动
                            view.offsetLeftAndRight(0 - mSpeed);
                        } else {//如果在等待队列,则更新每行最新的发射View

                            Log.e("WaitingTest", "view在等待队列");
                            InnerEntity innerEntity = (InnerEntity) view.getTag(R.id.tag_inner_entity);
                            int line = innerEntity.bestLine;
                            //取当前行最后发射的View,判断它是否滑出右边界
                            View currentLastView = mSpanList.get(line);
                            if(isViewLeaveRightBound(currentLastView)){//滑出右边界
                                mSpanList.set(line, view);
                                //从等待队列中移除
                                mWaitingViewsForLine.get(line).remove(view);
                            }
                        }

                    } else {
                        //滑出屏幕的View添加到缓存中
                        int type = ((InnerEntity) view.getTag(R.id.tag_inner_entity)).model.getType();
                        mAdapter.addViewToCache(type, view);
                        Log.e("ViewCacheSize", "回收View后:" + mAdapter.getCacheSize() + "");
                        BarrageView.this.removeView(view);

                    }
                }
            }

        }


    };

    /**
     * view是否等待发射
     *
     * @param view
     * @return
     */
    private boolean isInWaitingQueue(View view) {
        Set<Integer> keySet = mWaitingViewsForLine.keySet();
        for (Integer index : keySet) {
            Queue<View> queue = mWaitingViewsForLine.get(index);
            if (queue != null && queue.contains(view)) {
                return true;
            }
        }
        return false;
    }

    private class BackLoopHandler extends Handler {
        BackLoopHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpanList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        WIDTH = MeasureSpec.getSize(widthMeasureSpec);
        HEIGHT = MeasureSpec.getSize(heightMeasureSpec);
        //行数
        mSpanCount = HEIGHT / mSingleLineHeight;
        if(mWaitingViewsForLine == null){
            mWaitingViewsForLine = new HashMap<>();
            // 创建同样大小的view集合
            for (int i = 0; i < mSpanCount; i++) {
                if (mSpanList.size() < mSpanCount) {
                    mSpanList.add(i, null);
                }
                //初始化每一行的待发射View集合
                mWaitingViewsForLine.put(i, new LinkedList<View>());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setAdapter(BarrageAdapter adapter) {
        this.mAdapter = adapter;
        mSingleLineHeight = adapter.getSingleLineHeight();
        //开线程使弹幕滚动起来
        new Thread(new LoopRunnable()).start();
    }

    /**
     * 计算新新弹幕最佳的行数
     *
     * @return
     */
    private int getBestLine() {
        // 转换为2进制
        int gewei = mGravity % 2;
        int temp = mGravity / 2;
        int shiwei = temp % 2;
        temp = temp / 2;
        int baiwei = temp % 2;

        // 将所有的行分为三份,前两份行数相同,将第一份的行数四舍五入
        int firstLine = (int) (mSpanCount / 3.0 + 0.5);
        //根据gravity，计算支持的行索引集合
        List<Integer> legalLines = new ArrayList<>();
        //上
        if (gewei == 1) {
            for (int i = 0; i < firstLine; i++) {
                legalLines.add(i);
            }
        }
        //中
        if (shiwei == 1) {
            for (int i = firstLine; i < firstLine * 2; i++) {
                legalLines.add(i);
            }
        }
        //下
        if (baiwei == 1) {
            for (int i = firstLine * 2; i < mSpanCount; i++) {
                legalLines.add(i);
            }
        }
        int bestLine = 0;
        // 如果有空行,将空行返回
        for (int i = 0; i < mSpanCount; i++) {
            //空行
            if (mSpanList.get(i) == null) {
                bestLine = i;
                if (legalLines.contains(bestLine)) {
                    return bestLine;
                }
            }
        }

        float minSpace = Integer.MAX_VALUE;
        // 没有空行，就找最大空间的
        for (int i = mSpanCount - 1; i >= 0; i--) {
            if (legalLines.contains(i)) {
                if (mSpanList.get(i).getX() + mSpanList.get(i).getWidth() <= minSpace) {
                    minSpace = mSpanList.get(i).getX() + mSpanList.get(i).getWidth();
                    bestLine = i;
                }
            }
        }

        return bestLine;
    }

    /**
     * 添加view
     */
    public void addTypeView(BarrageModel model, View child, boolean isReused) {
        if (child == null) {
            return;
        }
        super.addView(child);
        child.measure(MeasureSpec.UNSPECIFIED, 0);
        //把宽高拿到，宽高都是包含ItemDecorate的尺寸
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        //获取最佳行数
        int bestLine = getBestLine();
        // 设置子view位置
        child.layout(WIDTH, mSingleLineHeight * bestLine, WIDTH + width, mSingleLineHeight * bestLine + height);
        InnerEntity innerEntity = null;
        innerEntity = (InnerEntity) child.getTag(R.id.tag_inner_entity);
        if (!isReused || innerEntity == null) {
            innerEntity = new InnerEntity();
        }
        innerEntity.model = model;
        innerEntity.bestLine = bestLine;
        child.setTag(R.id.tag_inner_entity, innerEntity);
        if (mSpanList.get(bestLine) == null) {//当前行为空行
            mSpanList.set(bestLine, child);
            return;
        }
        //当前行不为空行
        //获取当前这一行最后已经发射的View
        View currentLastestView = mSpanList.get(bestLine);
        if (isViewLeaveRightBound(currentLastestView)) {
            //如果已经离开右边界,则更新mSpanList
            mSpanList.set(bestLine, child);
        } else {
            //如果还没有离开右边界,则将新Child加入等待集合,等待当前行最后发射的View离开右边屏幕
            Queue<View> views = mWaitingViewsForLine.get(bestLine);
            views.add(child);
        }
    }


    /**
     * 判断child vieew是否离开左边的屏幕
     *
     * @param view
     * @return
     */
    private boolean isViewLeaveLeftBound(View view) {
        return view.getX() + view.getWidth() <= 0;
    }

    /**
     * 判断child vieew是否离开右边的屏幕
     *
     * @param view
     * @return
     */
    private boolean isViewLeaveRightBound(View view) {
        return view.getX() + view.getWidth() <= getWidth();
    }

    /**
     * 添加弹幕view
     *
     * @param model
     */
    public void addDanmu(final BarrageModel model) {
        if (mAdapter == null) {
            throw new Error("Adapter(an interface need to be implemented) can't be null,you should call setAdapter firstly");
        }

        View dmView = null;
        int cacheSize = mAdapter.getCacheSize();
        if (cacheSize > 0) {
            int type = model.getType();
            View cacheView = mAdapter.removeViewFromCache(type);
            if (cacheView != null) {
                Log.e("ViewCacheSize", "复用View后:" + mAdapter.getCacheSize() + "");
                dmView = cacheView;
                addTypeView(model, dmView, true);
            } else {
                dmView = mAdapter.getView(model, null);
                addTypeView(model, dmView, false);
            }

        } else {
            dmView = mAdapter.getView(model, null);
            addTypeView(model, dmView, false);
        }

//        dmView = mAdapter.getView(model, null);
//        addTypeView(model, dmView, false);

        //添加监听
        dmView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(model);
            }
        });
    }

    /**
     * 循环轮询，移动发送消息移动子View
     */
    private class LoopRunnable implements Runnable {
        @Override
        public void run() {
            int count = 0;
            Message msg = null;
            while (!mIsQuit) {
                if (count < 7500) {
                    count++;
                } else {
                    count = 0;
                    //收缩缓存
                    if (BarrageView.this.getChildCount() < mAdapter.getCacheSize() / 2) {
                        mAdapter.shrinkCacheSize();
                    }
                }
                if (BarrageView.this.getChildCount() >= 0) {
                    msg = new Message();
                    msg.what = 1; //移动view
                    mMainMoveHandler.sendMessage(msg);
                }

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * View被销毁的时候，终止轮询线程，清理集合
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsQuit = true;
        mAdapter.clearCache();
        if (mSpanList != null && mSpanList.size() > 0) {
            mSpanList.clear();
        }
        mWaitingViewsForLine.clear();;
    }

    class InnerEntity {
        public int bestLine;
        public BarrageModel model;
    }
}
