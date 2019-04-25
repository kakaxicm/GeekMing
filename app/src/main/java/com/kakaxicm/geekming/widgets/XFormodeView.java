package com.kakaxicm.geekming.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class XFormodeView extends View {

    private Paint paint;
    private Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private RectF rectF;
    private int rad;
    private float percent = 0.7f;

    public XFormodeView(Context context) {
        this(context, null);
    }

    public XFormodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XFormodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        int width = 500;
        int height = 100;
        rad = height / 2;
        rectF = new RectF(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int layerID = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(rectF, rad, rad, paint);
        paint.setXfermode(mode);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectF.width() * percent, rectF.top, rectF.right, rectF.bottom, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(layerID);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //禁用硬件加速
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        //使用离屏绘制
//        int layerID = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.ALL_SAVE_FLAG);
//        int width = getWidth();
//        int height = getHeight();
//        canvas.drawBitmap(createDstBigmap(width, height), 0, 0, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(createSrcBigmap(width, height), width / 2, height / 2, paint);
//        paint.setXfermode(null);
//
//        canvas.restoreToCount(layerID);
//
//    }

//    public Bitmap createDstBigmap(int width, int height) {
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint scrPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        scrPaint.setColor(0xFFFFCC44);
//        canvas.drawCircle(width / 2, height / 2, width / 2, scrPaint);
//        return bitmap;
//    }
//
//    public Bitmap createSrcBigmap(int width, int height) {
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint dstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        dstPaint.setColor(0xFF66AAFF);
//        canvas.drawRect(new Rect(0, 0, width, height), dstPaint);
//        return bitmap;
//    }
}
