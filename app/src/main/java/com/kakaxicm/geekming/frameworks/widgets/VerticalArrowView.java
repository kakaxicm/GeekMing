package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.kakaxicm.geekming.utils.SizeUtils;

/**
 * Created by star on 15/12/30.
 */
public class VerticalArrowView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    //path相关
    private Path mPath1;
    private Path mPath2;
    private int mPathWidth;
    private Point mVertextPoint;
    private Point mBottomPoint1;
    private Point mBottomPoint2;
    private int mVertextPontY;
    //顶点的垂直运动范围
    private int MAXVERTEXY;
    private int MINCVERTEXY;
    private int LINEVERTICALGAP;//path之间的GAP

    private float VERTEXT_FRACTION = 0.8f;
    private float LINES_GAP_FRACTION = 0.2f;

    public VerticalArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //wrapcontent处理 当lp为wrapcontent的时候，则最终测量的大小是父布局剩余空间大小
        if(widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            int defaultSize = (int) SizeUtils.dp2Px(getContext().getResources(), 16f);
            setMeasuredDimension(defaultSize, defaultSize);
        }
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        initPathParams();
    }

    private void initPathParams() {
        mPathWidth = (int) (VERTEXT_FRACTION*mWidth);
        mPath1 = new Path();
        mPath2 = new Path();

        MINCVERTEXY = (int) (mHeight*(1 - VERTEXT_FRACTION));
        MAXVERTEXY = (int) (mHeight* VERTEXT_FRACTION);
        LINEVERTICALGAP = (int) (mHeight*LINES_GAP_FRACTION);

        int verTexY = MINCVERTEXY;
        mVertextPontY = verTexY;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath1.reset();//注意这句话必须！否则path没有清楚，会保留之前的line和point，画出来是个图形
        mPath2.reset();

        mVertextPoint = new Point(mWidth/2, mVertextPontY);
        int bottomPointY = (mHeight/2 + LINEVERTICALGAP/2);
        int bottomPointX1 = (mWidth - mPathWidth)/2;
        int bottomPointX2 = (mWidth + mPathWidth)/2;

        mBottomPoint1 = new Point(bottomPointX1, bottomPointY);
        mBottomPoint2 = new Point(bottomPointX2, bottomPointY);

        mPath1.moveTo(mBottomPoint1.x, mBottomPoint1.y);
        mPath1.lineTo(mVertextPoint.x, mVertextPoint.y);
        mPath1.lineTo(mBottomPoint2.x, mBottomPoint2.y);

        mPath2.moveTo(mBottomPoint1.x, mBottomPoint1.y - LINEVERTICALGAP);
        mPath2.lineTo(mVertextPoint.x, mVertextPoint.y - LINEVERTICALGAP);
        mPath2.lineTo(mBottomPoint2.x, mBottomPoint2.y - LINEVERTICALGAP);
        canvas.drawPath(mPath1, mPaint);
        canvas.drawPath(mPath2, mPaint);

    }

    /**
     * title垂直滑动时的path变换
     * @param percent
     */
    public void onScrollPercent(float percent) {
        mVertextPontY = (int) ((MAXVERTEXY - MINCVERTEXY) * percent + MINCVERTEXY);
        invalidate();
    }
}
