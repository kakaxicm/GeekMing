package com.kakaxicm.geekming.frameworks.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.utils.SizeUtils;

/**
 * Created by chenming on 2018/8/28
 */
public class RulerView extends View {

    private int mBgColor = Color.parseColor("#a4a2a2");

    //刻度的颜色
    private int mCalibrationColor = Color.WHITE;
    //尺子上的文本颜色
    private int mTextColor = Color.WHITE;
    //三角形颜色
    private int mTriangleColor = Color.WHITE;
    //文本大小
    private float mTextSize = 14.0f; //sp
    //直尺上的文本垂直位置
    private float mTextY;
    //刻度线的宽度
    private float mCalibrationWidth = 1.0f; //dp
    //短的刻度线的高度
    private float mCalibrationShort = 20; //dp
    //长的刻度线的高度
    private float mCalibrationLong = 35; //dp
    //三角形高度
    private float mTriangleHeight = 18.0f; //dp
    //当前View的宽度
    private int mWidth;
    //宽度的中间值
    private int mMiddle;
    //刻度尺最小值以per为单位
    private float mMinValue = 0;
    //最大值
    private float mMaxValue = 100;
    //刻度尺当前值
    private float mValue = 0;
    //每一格代表的值
    private float mPer = 1;
    //两条长的刻度线之间的 mPer 数量
    private int mPerCount = 10;
    //当前刻度与最小值的距离 (minValue-value)/mPer*mGapWidth
    private float mOffsetFromValue;
    //当前刻度与最新值的最大距离 (minValue-maxValue)/mPer*mGapWidth
    private float mMaxOffset;
    //两个刻度之间的距离
    private float mGapWidth = 10.0f; //dp
    //总的刻度数量
    private int mTotalCalibration;
    //滑动中的水平位置
    private float mLastX;
    //被认为是快速滑动的最小速度
    private float mMinFlingVelocity;
    //处理fling手势帮助类
    private Scroller mScroller;
    //滑动过程的偏移
    private float mDelta;
    //画笔
    private Paint mPaint;
    //速度追踪器
    private VelocityTracker mVelocityTracker;
    //对外监听
    private OnValueChangeListener mOnValueChangeListener;

    //默认的宽高
    private int mDefaultWidth;
    private int mDefaultHeight;

    /**
     * 回调接口
     */
    public interface OnValueChangeListener {
        void onChange(float value);
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    private Context mContext;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(context, attrs);
        init(context);
        calculateAttr();
    }

    /**
     * 初始化滚动方面的参量
     *
     * @param context
     */
    private void init(Context context) {
        mMinFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        mScroller = new Scroller(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);

        mDefaultWidth = (int) SizeUtils.dp2px(280, mContext);
        mDefaultHeight = (int) SizeUtils.dp2px(80, mContext);
    }

    /**
     * 读取布局文件中的自定义属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        mBgColor = ta.getColor(R.styleable.RulerView_bgColor, mBgColor);
        mCalibrationColor = ta.getColor(R.styleable.RulerView_calibrationColor, mCalibrationColor);
        mCalibrationWidth = ta.getDimension(R.styleable.RulerView_calibrationWidth, SizeUtils.dp2px(mCalibrationWidth, context));
        mCalibrationLong = ta.getDimension(R.styleable.RulerView_calibrationLong, SizeUtils.dp2px(mCalibrationLong, context));
        mCalibrationShort = ta.getDimension(R.styleable.RulerView_calibrationShort, SizeUtils.dp2px(mCalibrationShort, context));
        mTriangleColor = ta.getColor(R.styleable.RulerView_triangleColor, mTriangleColor);
        mTriangleHeight = ta.getDimension(R.styleable.RulerView_triangleHeight, SizeUtils.dp2px(mTriangleHeight, context));
        mTextColor = ta.getColor(R.styleable.RulerView_textColor, mTextColor);
        mTextSize = ta.getDimension(R.styleable.RulerView_textSize, SizeUtils.sp2px(mTextSize, context));
        mPer = ta.getFloat(R.styleable.RulerView_per, mPer);
        mPer *= 10.0f;
        mPerCount = ta.getInt(R.styleable.RulerView_perCount, mPerCount);
        mGapWidth = ta.getDimension(R.styleable.RulerView_gapWidth, SizeUtils.dp2px(mGapWidth, context));
        mMinValue = ta.getFloat(R.styleable.RulerView_minValue, mMinValue);
        mMaxValue = ta.getFloat(R.styleable.RulerView_maxValue, mMaxValue);
        mValue = ta.getFloat(R.styleable.RulerView_value, mValue);
        ta.recycle();
    }

    private void calculateAttr() {
        verifyValues(mMinValue, mValue, mMaxValue);
        mTextY = mCalibrationLong + SizeUtils.dp2px(30, mContext);
        //物理数值转化为最小gap的数量
        mOffsetFromValue = (mValue - mMinValue) * 10 / mPer * mGapWidth;
        mMaxOffset = (mMaxValue - mMinValue) * 10.0f / mPer * mGapWidth;
        mTotalCalibration = (int) ((mMaxValue - mMinValue) * 10.0f / mPer + 1);
    }

    /**
     * 修正minValue，value，maxValue 的有效性
     *
     * @param minValue
     * @param value
     * @param maxValue
     */
    private void verifyValues(float minValue, float value, float maxValue) {
        if (minValue > maxValue) {
            mMinValue = maxValue;
        }

        if (value < minValue) {
            mValue = minValue;
        }

        if (value > maxValue) {
            mValue = maxValue;
        }
    }

    /**
     * onMesaure流程代码
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Step1:拿到父View期望的大小
        int resultWidth = 0;
        int resultHeight = 0;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //先赋值
        resultWidth = widthSize;
        resultHeight = heightSize;
        //Step2:wrap_content处理
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //在这里实现计算需要wrap_content时需要的宽
            resultWidth = mDefaultWidth;
        }
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //在这里实现计算需要wrap_content时需要的高
            resultHeight = mDefaultHeight;
        }
        //step3:设置测量结果
        setMeasuredDimension(resultWidth, resultHeight);
        mWidth = resultWidth;
        mMiddle = mWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBgColor);
        drawCalibration(canvas);
        drawTriangle(canvas);
    }

    /**
     * 先根据当前值到minValue的offset和宽度的一半计算当前起始的刻度位置x,然后得到实际的起始value
     * 再依次绘制刻度和文本
     *
     * @param canvas
     */
    private void drawCalibration(Canvas canvas) {
        float deltaX = mMiddle - mOffsetFromValue;
        float startCalibrationXOffset = 0;//当前尺子的起始刻度的绘制位置
        int startCalibrationIndex;//在整个尺子中，当前屏幕起始绘制位置startCalibrationXOffset所对应的刻度索引
        //1.deltaX > 0 表示尺子的原点已经出现在屏幕，那么起始位置就是deltaX.
        if (deltaX > 0) {//尺子原点出现在屏幕,起始刻度的偏移为deltaX,计算起始索引
            startCalibrationXOffset = deltaX;
            startCalibrationIndex = 0;//从原点开始绘制, startCalibrationXOffset对应的刻度索引为0
        } else {//2. deltaX<0,则表示尺子的原点在屏幕左侧，需要计算屏幕最左边的刻度索引
            //运算的迭代变量
            int tempIndex = 0;//刻度索引
            float tempOffset = deltaX;
            //循环累加，直到tempOffset>0,此时起始的绘制刻度索引为tempIndex，绘制位置为tempOffset
            while (tempOffset < 0) {
                tempIndex++;
                tempOffset = deltaX + tempIndex * mGapWidth;
            }
            startCalibrationXOffset = tempOffset;
            startCalibrationIndex = tempIndex;
        }

        //确定了起始的刻度索引和绘制位置,开始绘制刻度，注意长刻度需要绘制文本
        int drawCalibrationIndex = startCalibrationIndex;
        float drawCalibrationXOffset = startCalibrationXOffset;
        while (drawCalibrationIndex < mTotalCalibration && drawCalibrationXOffset < mWidth) {
            float startX = drawCalibrationXOffset;
            float height;//刻度线高度
            String value;
            if (drawCalibrationIndex % mPerCount == 0) {
                height = mCalibrationLong;
                mPaint.setStrokeWidth(mCalibrationWidth * 2);
                //根据drawCalibrationIndex计算当前的物理浮点数值
                value = String.valueOf(mMinValue + drawCalibrationIndex * mPer / 10.0f);
                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - 2);
                }
                //绘制文本
                canvas.drawText(value, startX - mPaint.measureText(value) / 2, mTextY, mPaint);
            } else {
                height = mCalibrationShort;
                mPaint.setStrokeWidth(mCalibrationWidth);
            }

            mPaint.setColor(mCalibrationColor);

            canvas.drawLine(startX, 0, startX, height, mPaint);
            drawCalibrationIndex++;
            drawCalibrationXOffset = drawCalibrationXOffset + mGapWidth;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = x;
                mDelta = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                //向右滑为负值
                mDelta = mLastX - x;
                Log.e("TAG", mDelta + "");
                //更新value和 mOffsetFromValue 重绘制
                updateValueAndOffset();
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                //松手的时候，一定不要忘记复位触摸事件的参量，以便scroller做准确计算
                mLastX = 0;
                mDelta = 0;
                //滚动参量清零后，检测是否为飞速滑动，如果是则scroller执行filing方法,否则做对齐动画操作
                handlerSmoothAction();
                return false;
            default:
                return false;
        }

        return true;
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * fling弹性滑动支持+弹性滚动到指定刻度
     */
    private void handlerSmoothAction() {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity(); //计算水平方向的速度（单位秒）
        if (Math.abs(xVelocity) > mMinFlingVelocity) {//飞速手势
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            invalidate();
        } else {
            //滑动到最近的刻度位置
            smoothMoveToFinalCalibration();
            recycleVelocityTracker();
        }
    }

    @Override
    public void computeScroll() {
        //返回true表示滑动还没有结束
        if (mScroller.computeScrollOffset()) {
            //fling手势滚动结束
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                //滑动到最近的刻度位置
                recycleVelocityTracker();
                //平滑实现刻度对齐
                smoothMoveToFinalCalibration();
            } else {
                //飞速滚动过程中实现更新offset
                int x = mScroller.getCurrX();
                mDelta = mLastX - x;
                updateValueAndOffset();
                mLastX = x;
            }
        }
    }

    /**
     * 滑动结束，平滑移动到对准刻度的位置,利用属性动画实现平滑对齐
     */
    private void smoothMoveToFinalCalibration() {
        //value是准确对准刻度的值
        mValue = mMinValue + Math.round(mOffsetFromValue / mGapWidth) * mPer / 10.0f;
        //intOffset为value对准的刻度偏移
        float intOffset = (mValue - mMinValue) * 10.0f / mPer * mGapWidth;
        //offset在value对应的刻度附近,不一定准确对准刻度
        float startX = mOffsetFromValue;
        float endX = intOffset;
        //动画实现平滑对齐
        ValueAnimator animator = ValueAnimator.ofFloat(startX, endX);
        animator.setDuration((long) (300 * Math.abs(endX - startX) / mGapWidth));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetFromValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener.onChange(mValue);
        }
    }


    /**
     * 更新偏移，重新绘制
     */
    private void updateValueAndOffset() {
        //更新offset
        //防止越界
        mOffsetFromValue += mDelta;
        if (mOffsetFromValue < 0) {
            mScroller.forceFinished(true);
            mOffsetFromValue = 0;
            mDelta = 0;

        } else if (mOffsetFromValue > mMaxOffset) {
            mOffsetFromValue = mMaxOffset;
            mDelta = 0;
            mScroller.forceFinished(true);
        }
//        offset更新，value发生变化
//        公式:mOffsetFromValue = (value - minValue) * 10 / mPer * mGapWidth;
//        value = minValue + Math.round((mOffsetFromValue * mPer / mGapWidth) / 10.0f);
        mValue = mMinValue + Math.round(mOffsetFromValue / mGapWidth) * mPer / 10.0f;
        Log.e("Value", mValue + "");
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener.onChange(mValue);
        }
        invalidate();
    }

    /**
     * 用path绘制三角形
     *
     * @param canvas
     */
    private void drawTriangle(Canvas canvas) {
        mPaint.setColor(mTriangleColor);
        Path path = new Path();
        path.moveTo(getWidth() / 2 - mTriangleHeight / 2, 0);
        path.lineTo(getWidth() / 2, mTriangleHeight / 2);
        path.lineTo(getWidth() / 2 + mTriangleHeight / 2, 0);
        path.close();
        canvas.drawPath(path, mPaint);
    }
}
