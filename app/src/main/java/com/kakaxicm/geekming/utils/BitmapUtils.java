package com.kakaxicm.geekming.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtils {
    public static String TEMP_FOLDER = "magic_sign/temp";
    private static final String LOG_TAG = BitmapUtils.class.getSimpleName();

    /**
     * 从资源文件获取bitmap
     */
    public static Bitmap getBitmap(Context context, int resId) {
        return getBitmap(context, resId, null);
    }

    /**
     * 从资源文件获取bitmap
     */
    public static Bitmap getBitmap(Context context, int resId, Options option) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId, option);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static String getTempSaveDir() {
        String userDir = "";
        if (isSDCardMounted()) {
            userDir = Environment.getExternalStorageDirectory().toString() + System.getProperty("file.separator");
        } else {
            userDir = System.getProperty("file.separator");
        }
        File file = new File(userDir + TEMP_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/";
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    }

    /**
     * 将bitmap存成文件
     *
     * @param context
     * @param bitmap
     * @param fileName
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String fileName) {
        if (context == null || bitmap == null || TextUtils.isEmpty(fileName)) {
            return null;
        }
        String ret = null;
        OutputStream fos = null;
        try {

            Uri uri = Uri.fromFile(new File(BitmapUtils.getTempSaveDir() + fileName));
            File file = new File(uri.getPath());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fos = context.getContentResolver().openOutputStream(uri);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            }
            ret = getTempSaveDir() + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * @param bitmap   The picture intent to add round corner
     * @param radius   The radius of the corner
     * @param location Which corner to add round corner:
     *                 1-left_top;2-left_bottom;3-right_top;4-right_bottom
     * @return The picture with round corner
     * @author huyongsheng
     */
    public static Bitmap getRoundCorner(Bitmap bitmap, int radius, int... location) {

        if (bitmap == null) {
            return bitmap;
        }

        final int LEFT_TOP = 1;
        final int LEFT_BOTTOM = 2;
        final int RIGHT_TOP = 3;
        final int RIGHT_BOTTOM = 4;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Rect completeRect = new Rect(0, 0, width, height);
        Rect leftTopRect = new Rect(0, 0, radius, radius);
        Rect leftBottomRect = new Rect(0, height - radius, radius, height);
        Rect rightTopRect = new Rect(width - radius, 0, width, radius);
        Rect rightBottomRect = new Rect(width - radius, height - radius, width, height);
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawRect(completeRect, paint);
        if (location != null) {
            for (int corner : location) {
                switch (corner) {
                    case LEFT_TOP:
                        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                        canvas.drawRect(leftTopRect, paint);
                        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
                        canvas.drawCircle(radius, radius, radius, paint);
                        break;
                    case LEFT_BOTTOM:
                        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                        canvas.drawRect(leftBottomRect, paint);
                        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
                        canvas.drawCircle(radius, height - radius, radius, paint);
                        break;
                    case RIGHT_TOP:
                        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                        canvas.drawRect(rightTopRect, paint);
                        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
                        canvas.drawCircle(width - radius, radius, radius, paint);
                        break;
                    case RIGHT_BOTTOM:
                        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                        canvas.drawRect(rightBottomRect, paint);
                        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
                        canvas.drawCircle(width - radius, height - radius, radius, paint);
                        break;
                    default:
                        break;
                }
            }
        }
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    /**
     * 获取空图片,如果bitmap创建失败返回null
     */
    public static Bitmap getEmptyBitmap(Context context, int width, int height) {
        return getEmptyBitmap(context, width, height, Color.BLACK);
    }

    /**
     * 获取空图片,如果bitmap创建失败返回null
     */
    public static Bitmap getEmptyBitmap(Context context, int width, int height, int color) {
        return getEmptyBitmap(context, width, height, color, Config.ARGB_8888);
    }

    /**
     * 获取空图片,如果bitmap创建失败返回null
     */
    public static Bitmap getEmptyBitmap(Context context, int width, int height, int color, Config config) {
        Bitmap bitmap = null;
        if (width * height > 0) {
            try {
                bitmap = Bitmap.createBitmap(width, height, config);
            } catch (OutOfMemoryError e) {
                LogUtils.e(context, LOG_TAG, "can not create bitmap because lack of memory.");
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            // 创建canvas `   
            Canvas canvas = new Canvas(bitmap);
            // 清空画布
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
            // 绘制颜色
            canvas.drawColor(color);
        }
        return bitmap;
    }

    /**
     * 获取指定尺寸的bitmap
     */
    public static Bitmap getBitmap(Context context, Bitmap bitmap, int w, int h) {
        Bitmap result = null;
        Matrix matrix = new Matrix();
        float scale = Math.min(((float) w / bitmap.getWidth()), ((float) h / bitmap.getHeight()));
        matrix.postScale(scale, scale);
        matrix.postTranslate((w - bitmap.getWidth() * scale) / 2, (h - bitmap.getHeight() * scale) / 2);
        try {
            result = getEmptyBitmap(context, w, h, Color.TRANSPARENT, Config.ARGB_8888);
            // 创建canvas `   
            Canvas canvas = new Canvas(result);
            // 根据变换矩阵绘制
            canvas.drawBitmap(bitmap, matrix, null);
        } catch (Exception e) {
            LogUtils.e(context, LOG_TAG, "can not create bitmap because lack of memory.");
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap getContentViewShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        int width = SizeUtils.getScreenWidth(activity);
        int height = SizeUtils.getScreenHeight(activity);

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        Bitmap srcBp = view.getDrawingCache();
        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(srcBp, 0,
                0, width, height);
//        // 去掉状态栏
//        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
//                statusBarHeights, width, height - statusBarHeights);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }

}
