package com.kakaxicm.geekming.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kakaxicm.geekming.R;

public class XfermodeReLearnActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView iv = new MaskImageView(this);
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setImageResource(R.drawable.heartmap);
        setContentView(iv);
    }

    private class MaskImageView extends android.support.v7.widget.AppCompatImageView {
        private Paint maskPaint;//蒙层

        private Path path;

        private float lastX;
        private float lastY;
        private Xfermode model = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        public MaskImageView(Context context) {
            super(context);
            maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            maskPaint.setStrokeWidth(20);
            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), maskPaint, Canvas.ALL_SAVE_FLAG);
            maskPaint.setColor(Color.GRAY);
            maskPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, getWidth(), getHeight(), maskPaint);//先画目标蒙层
            maskPaint.setStyle(Paint.Style.STROKE);
            maskPaint.setStrokeCap(Paint.Cap.ROUND);
            maskPaint.setXfermode(model);
            canvas.drawPath(path, maskPaint);
            maskPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    path.moveTo(lastX, lastY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float endX = (lastX + event.getX()) / 2;
                    float endY = (lastY + event.getY()) / 2;
                    path.quadTo(lastX, lastY, endX, endY);
                    lastX = endX;
                    lastY = endY;
                    break;
                case MotionEvent.ACTION_UP:
                    lastX = 0;
                    lastY = 0;
                    break;

            }
            invalidate();
            return true;
        }
    }
}
