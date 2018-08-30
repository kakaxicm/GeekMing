package com.kakaxicm.geekming.barrage;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.kakaxicm.geekming.R;

import java.util.ArrayList;
import java.util.List;

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

    private int gravity = GRAVITY_FULL;

    private int speed = 4;

    private int spanCount = 6;

    private int WIDTH, HEIGHT;

    private int singltLineHeight;

    private BarrageAdapter adapter;

    public List<View> spanList;//每一行新加进来的view集合

    private OnItemClickListener onItemClickListener;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                for (int i = 0; i < BarrageView.this.getChildCount(); i++) {
                    View view = BarrageView.this.getChildAt(i);
                    if (view.getX() + view.getWidth() >= 0)
                        // 向左滑动
                        view.offsetLeftAndRight(0 - speed);
                    else {
                        //滑出屏幕的View添加到缓存中
                        int type = ((InnerEntity) view.getTag(R.id.tag_inner_entity)).model.getType();
//                        adapter.addViewToCache(type, view);
                        BarrageView.this.removeView(view);

                    }
                }
            }

        }
    };

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        spanList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        WIDTH = MeasureSpec.getSize(widthMeasureSpec);
        HEIGHT = MeasureSpec.getSize(heightMeasureSpec);

        spanCount = HEIGHT / singltLineHeight;
        // 创建同样大小的view集合
        for (int i = 0; i < spanCount; i++) {
            if (spanList.size() < spanCount) {
                spanList.add(i, null);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setAdapter(BarrageAdapter adapter) {
        this.adapter = adapter;
        singltLineHeight = adapter.getSingleLineHeight();
        //TODO  开线程使弹幕滚动起来
        new Thread(new MyRunnable()).start();
    }

    /**
     * 计算新新弹幕最佳的行数
     *
     * @return
     */
    private int getBestLine() {
        // 转换为2进制
        int gewei = gravity % 2;
        int temp = gravity / 2;
        int shiwei = temp % 2;
        temp = temp / 2;
        int baiwei = temp % 2;

        // 将所有的行分为三份,前两份行数相同,将第一份的行数四舍五入
        int firstLine = (int) (spanCount / 3.0 + 0.5);
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
            for (int i = firstLine * 2; i < spanCount; i++) {
                legalLines.add(i);
            }
        }
        int bestLine = 0;
        // 如果有空行,将空行返回
        for (int i = 0; i < spanCount; i++) {
            //空行
            if (spanList.get(i) == null) {
                bestLine = i;
                if (legalLines.contains(bestLine)) {
                    return bestLine;
                }
            }
        }

        float minSpace = Integer.MAX_VALUE;
        // 没有空行，就找最大空间的
        for (int i = spanCount - 1; i >= 0; i--) {
            if (legalLines.contains(i)) {
                if (spanList.get(i).getX() + spanList.get(i).getWidth() <= minSpace) {
                    minSpace = spanList.get(i).getX() + spanList.get(i).getWidth();
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
        child.layout(WIDTH, singltLineHeight * bestLine, WIDTH + width, singltLineHeight * bestLine + height);
        InnerEntity innerEntity = null;
        innerEntity = (InnerEntity) child.getTag(R.id.tag_inner_entity);
        if (!isReused || innerEntity == null) {
            innerEntity = new InnerEntity();
        }
        innerEntity.model = model;
        innerEntity.bestLine = bestLine;
        child.setTag(R.id.tag_inner_entity, innerEntity);

        spanList.set(bestLine, child);
    }

    /**
     * 添加弹幕view
     *
     * @param model
     */
    public void addDanmu(final BarrageModel model) {
        if (adapter == null) {
            throw new Error("Adapter(an interface need to be implemented) can't be null,you should call setAdapter firstly");
        }

        View dmView = null;
        dmView = adapter.getView(model, null);
        addTypeView(model, dmView, false);

        //添加监听
        dmView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(model);
            }
        });
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            int count = 0;
            Message msg = null;
            while (true) {
                if (count < 7500) {
                    count++;
                } else {
                    count = 0;
//                    if (BarrageView.this.getChildCount() < adapter.getCacheSize() / 2) {
//                        adapter.shrinkCacheSize();
//                        System.gc();
//                    }
                }
                if (BarrageView.this.getChildCount() >= 0) {
                    msg = new Message();
                    msg.what = 1; //移动view
                    handler.sendMessage(msg);
                }

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class InnerEntity {
        public int bestLine;
        public BarrageModel model;
    }
}
