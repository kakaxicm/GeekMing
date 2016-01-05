package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.kakaxicm.geekming.R;

/**
 * Created by star on 16/1/5.
 * 仿照微信的气泡样式图片
 */
public class BitmapShapeView extends View{
    private Paint mPaint;
    private Path mPath;//包裹图片的那个不规则气泡给需要自己用Path实现
    private BitmapShader mBitmapShader;//填充起泡的渲染器
    private Bitmap mBitmap;

    private int mBubbleRadius = 16;
    private int mTriangleSize = 16;
    public BitmapShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mPaint = new Paint();
        mPath = new Path();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BubbleImageView);
        mBubbleRadius = array.getDimensionPixelSize(R.styleable.BubbleImageView_bubbleRadius, 16);
        mTriangleSize = array.getDimensionPixelSize(R.styleable.BubbleImageView_bubbleTriangleSize, 16);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mBitmapShader);
        canvas.drawPath(mPath, mPaint);

        mPaint.reset();
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
        mPath.reset();
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mPath.moveTo(0, mBubbleRadius);
        mPath.arcTo(new RectF(0, 0, mBubbleRadius*2, mBubbleRadius*2), 180, 90);
        mPath.lineTo(width - mBubbleRadius + mTriangleSize, 0);
        mPath.arcTo(new RectF(width - mBubbleRadius*2 - mTriangleSize, 0, width - mTriangleSize, mBubbleRadius*2), -90, 90);
        //画三角
        mPath.lineTo(width - mTriangleSize, height/2 - mTriangleSize/2);
        mPath.lineTo(width, height/2);
        mPath.lineTo(width - mTriangleSize, height/2 + mTriangleSize/2);
        mPath.lineTo(width - mTriangleSize, height - mBubbleRadius);
        mPath.arcTo(new RectF(width - 2*mBubbleRadius - mTriangleSize, height - mBubbleRadius*2, width - mTriangleSize, height), 0, 90);
        mPath.lineTo(mBubbleRadius, height);
        mPath.arcTo(new RectF(0, height - mBubbleRadius*2, mBubbleRadius*2, height), 90, 90);
        mPath.close();
    }

    public void setImageSrc(String path) {
        mBitmap = BitmapFactory.decodeFile(path);
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        invalidate();
    }

    public void setImageSrc(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }
}
