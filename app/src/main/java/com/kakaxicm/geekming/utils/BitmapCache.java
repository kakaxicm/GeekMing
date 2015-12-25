package com.kakaxicm.geekming.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * Created by superwz on 14/12/1.
 */
public class BitmapCache {
    private static BitmapCache instance = null;
    private LruCache<String, Bitmap> mLruCache;

    private BitmapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置图片缓存大小为maxMemory的1/6
        int cacheSize = maxMemory / 10;//20480000; //10MB;
        LogForTest.logE(BitmapCache.class.getSimpleName(), "cacheSize = " + Integer.toString(cacheSize));
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

//    public static synchronized BitmapCache getInstance(){
//        if(instance == null){
//            instance = new BitmapCache();
//        }
//        return instance;
//    }

    // 获取应用程序最大可用内存


//    public static void cleanMemo() {
//        if (instance != null && instance.mLruCache != null) {
//            instance.mLruCache.evictAll();
////            instance.mLruCache = null;
//        }
//    }

    /**
     * 从LruCache缓存获取图片
     */
    public Bitmap getBitmapFromLruCache(String key) {
        if (TextUtils.isEmpty(key))
            return null;
        return mLruCache.get(key);
    }

    /**
     * 将图片存储到LruCache
     */
    public void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (TextUtils.isEmpty(key) || bitmap == null)
            return;
        if (getBitmapFromLruCache(key) == null) {
            mLruCache.put(key, bitmap);
        }
    }
}
