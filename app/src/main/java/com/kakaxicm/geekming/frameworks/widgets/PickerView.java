package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class PickerView extends View {

    private Context context;

    private List<String> mDataList;
    private int mCurrentSelected;

    private boolean isInit = false;

    private Paint mPaint;//绘制选中文本的画笔

    //绘制的参量
    //文本大小的最值
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 2.8f;
    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;
    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;
    private int mViewHeight;
    private int mViewWidth;
    private float mMoveLen;
    private Paint nPaint;

    //触摸事件参量
    private float mLastDownY;

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mDataList = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            mDataList.add("测试" + i);
        }
        mCurrentSelected = mDataList.size() / 2;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.BLACK);

        //第二个paint
        nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Paint.Style.FILL);
        nPaint.setTextAlign(Paint.Align.CENTER);
        nPaint.setColor(Color.parseColor("#999999"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        // 按照View的高度计算字体大小
        mMaxTextSize = mViewHeight / 7f;
        mMinTextSize = mMaxTextSize / 2.2f;
        isInit = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit) {
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        //TODO 绘制选中文本
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));

        float x = mViewWidth / 2;
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        mPaint.setTextSize(mMaxTextSize);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, mPaint);
        // 绘制上方data
        for (int i = 1; mCurrentSelected - i > 0 && i < 4; i++) {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelected + i) < mDataList.size() && i < 4; i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param canvas
     * @param pos    距离中间几个item
     * @param type   -1 上方文本
     */
    private void drawOtherText(Canvas canvas, int pos, int type) {
        //距离越远，比例越小
        float offset = pos * MARGIN_ALPHA * mMinTextSize + type * mMoveLen;//向上滑,
        float scale = parabola(mViewHeight / 4.0f, offset);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        nPaint.setTextSize(size);
        nPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));

        float y = offset * type + mViewHeight / 2.0f;
        Paint.FontMetricsInt fmi = nPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * pos),
                (float) (mViewWidth / 2.0), baseline, nPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        mLastDownY = event.getY();
    }

    private void doMove(float y) {
        mMoveLen += y - mLastDownY;
        float testd = MARGIN_ALPHA * mMinTextSize * (mDataList.size() / 2);
        Log.e("move", "" + mMoveLen + "testd:" + testd);
        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {//向下滑超过行高的一半
            //滑到边界
            if (mCurrentSelected == 0) {
                mLastDownY = y;
                invalidate();
                return;
            }
            mMoveLen -= MARGIN_ALPHA * mMinTextSize;
            mCurrentSelected--;
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
            if (mCurrentSelected == mDataList.size() - 1){
                mLastDownY = y;
                invalidate();
                return;
            }
            mMoveLen += MARGIN_ALPHA * mMinTextSize;
            mCurrentSelected++;
        }
        mLastDownY = y;
        invalidate();
    }

    public void setmDataList(List<String> mDataList) {
        if (mDataList != null && !mDataList.isEmpty()) {
            this.mDataList = mDataList;
            mCurrentSelected = mDataList.size() / 2;
            invalidate();
        }
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }
}
