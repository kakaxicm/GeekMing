package com.kakaxicm.geekming.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

public class ZoomImageView extends android.support.v7.widget.AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private ScaleGestureDetector mScaleGestureDetector;

    private final Matrix mScaleMatrix = new Matrix();
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private boolean once;
    private float initScale = 1.0f;
    private float SCALE_MAX = 4.0f;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (getDrawable() == null)
            return true;
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }

            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            Log.e("scaleFactor", "" + scaleFactor + ", scale=" + scale);
            //step0:屏幕中心为缩放点
//            mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
            //step1:detector中心缩放点
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBoundAndCenter();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    //step2 缩放中心点可变，宽高大于屏幕时,有可能出现白边,手动做偏移
    private void checkBoundAndCenter() {
        final RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();
        if (rectF.width() >= width) {//宽大于屏幕宽度,则避免出界
            if (rectF.left > 0) {//左边有空白
                deltaX = -rectF.left;
            }
            if (rectF.right < width) {//右边有空白
                deltaX = width - rectF.right;
            }
        } else {//宽小于屏幕则水平居中
            deltaX = width / 2 - (rectF.right - rectF.width() / 2);
        }

        if (rectF.height() >= height) {//高大于屏幕高度,则避免出界
            if (rectF.top > 0) {//上边有空白
                deltaY = -rectF.top;
            }
            if (rectF.bottom < height) {//右边有空白
                deltaY = height - rectF.height();
            }
        } else {
            deltaY = height / 2 - (rectF.bottom - rectF.height() / 2);
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            mScaleMatrix.mapRect(rectF);
        }
        Log.e("RectF", rectF.left + "," + rectF.top + "," + rectF.width() + "," + rectF.height());
        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event);
    }

    public float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    public void onGlobalLayout() {
        if (!once) {
            once = true;
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }
            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw;
            }

            if (dh > height && dw <= width) {
                scale = height * 1.0f / dh;
            }
            if (dw > width && dh > height) {
                scale = Math.min(dw * 1.0f / width, dh * 1.0f / height);
            }

            initScale = scale;
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
        }
    }
}
