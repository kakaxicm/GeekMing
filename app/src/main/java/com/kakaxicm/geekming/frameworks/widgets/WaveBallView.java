package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * 玻璃球中的水波纹效果 贝塞尔曲线+path + PorterDuffXfermode
 */
public class WaveBallView extends View {
    private int radius = dp2px(80);//球的半径
    private int padding;
    private Paint circlePaint;
    private Paint pathPaint;//绘制水箱的paint
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private int size;

    private Path path;//水箱的path
    private float ratio = 0.05f;//起始pregress

    private static final int WAVE_COUNT = 2;//整个view的波形个数
    private static final int MAX_AMPLITUDE = 15;
    private static final int WAVE_SPEED = 5;//波形每一帧移动的偏移 即速度
    private int waveStartOffset;//波浪起点,初始化为负值,避免看到残缺的波形
    private int waveCycle;//一个波形的长度

    private boolean isWaving;//是否开启波浪动效
    private Paint textPaint;

    public WaveBallView(Context context) {
        super(context, null);
    }

    public WaveBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Color.RED);
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.GREEN);
        pathPaint.setDither(true);
        pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));//交集部分,水箱部分在圆上面

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.YELLOW);
        circlePaint.setDither(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dp2px(24));
        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resultWidth;
        int resultHeight;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //先赋值
        resultWidth = widthSize;
        resultHeight = heightSize;

        //取padding最小值
        int hPadding = Math.min(getPaddingLeft(), getPaddingRight());
        int vPadding = Math.min(getPaddingTop(), getPaddingBottom());

        padding = Math.min(hPadding, vPadding);

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            resultWidth = 2 * radius + padding * 2;
        }

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            resultHeight = 2 * radius + padding * 2;
        }
        //View未正方形
        size = Math.min(resultWidth, resultHeight);
        radius = (size - 2 * padding) / 2;
        waveCycle = 2 * radius / WAVE_COUNT;
        waveStartOffset = -waveCycle;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //目标绘制的bp
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(bitmap);
        }
        bitmapCanvas.save();

        bitmapCanvas.translate(padding, padding);

        //绘制目标
        bitmapCanvas.drawCircle(radius, radius, radius, circlePaint);
        pathPaint.setColor(Color.BLACK);
        path.reset();

        drawPath();
        drawText();
        bitmapCanvas.restore();

        //得到的bp绘制出来
        canvas.drawBitmap(bitmap, 0, 0, null);

        if (!isWaving) {
            isWaving = true;
            //开始水平波动
            startWave();
        }
    }

    private void drawText() {
        int tmp = (int) (ratio * 100);
        String text = tmp + "%";
        float textW = textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float baseLine = radius;
        baseLine = baseLine - (fontMetrics.ascent + fontMetrics.descent) / 2;
        bitmapCanvas.drawText(text, radius - textW / 2, baseLine, textPaint);
    }

    private void drawPath() {
        configPath();
        //绘制源 - 水箱
        bitmapCanvas.drawPath(path, pathPaint);
    }

    /**
     * 配置path参数
     */
    private void configPath() {
        int basePointY = (int) ((1 - ratio) * 2 * radius);//波形0点得垂直坐标
        //右上方
        path.moveTo(2 * radius, basePointY);
        //右下方
        path.lineTo(2 * radius, 2 * radius);
        //左下边
        path.lineTo(0, 2 * radius);
        //左上边
        path.lineTo(0, basePointY);
        //移动到波浪起点
        path.lineTo(waveStartOffset, basePointY);
        int basePointX = waveStartOffset;
        int controlDeltaX = waveCycle / 4;
        int controlDeltaY = getCurrentAmp();
        //绘制波形 知道超出View宽度
        while (basePointX < 2 * radius) {
            path.quadTo(basePointX + controlDeltaX, basePointY - controlDeltaY, basePointX + waveCycle / 2, basePointY);
            basePointX += waveCycle / 2;
            path.quadTo(basePointX + controlDeltaX, basePointY + controlDeltaY, basePointX + waveCycle / 2, basePointY);
            basePointX += waveCycle / 2;
        }
        path.close();
    }

    /**
     * 计算进度下的振幅 简单处理 中间最大，两边为0
     *
     * @return
     */
    private int getCurrentAmp() {
        return (int) (-4 * MAX_AMPLITUDE * Math.pow(ratio - 0.5f, 2) + MAX_AMPLITUDE);
    }

    public void setProgress(float progress) {
        ratio = progress;
        invalidate();
    }

    public void startWave() {
        if (task != null) {
            taskHandler.removeCallbacks(task);
            task = null;
        }

        taskHandler.sendMessage(taskHandler.obtainMessage());
    }

    private WaveTask task;
    private Handler taskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (task == null) {
                task = new WaveTask();
            }
            postDelayed(task, 10);
        }
    };

    private class WaveTask implements Runnable {

        @Override
        public void run() {
            waveStartOffset += WAVE_SPEED;
            if (waveStartOffset >= 0) {
                waveStartOffset -= waveCycle;
            }
            invalidate();
            taskHandler.sendMessage(taskHandler.obtainMessage());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (task != null) {
            taskHandler.removeCallbacks(task);
            task = null;
        }
        isWaving = false;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
