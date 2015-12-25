package com.kakaxicm.geekming.utils;

import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * Created by huyongsheng on 14/12/1.
 */
public class TypefaceCache {
    private static TypefaceCache instance = null;
    private LruCache<String, Typeface> mLruCache;

    private TypefaceCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置图片缓存大小为maxMemory的1/6
        int cacheSize = maxMemory / 10;//20480000; //10MB;
        LogForTest.logE(TypefaceCache.class.getSimpleName(), "cacheSize = " + Integer.toString(cacheSize));
        mLruCache = new LruCache<String, Typeface>(cacheSize) {
        };
    }

    public static synchronized TypefaceCache getInstance() {
        if (instance == null) {
            instance = new TypefaceCache();
        }
        return instance;
    }

    /**
     * 从LruCache缓存获取图片
     */
    public Typeface getTypefaceFromLruCache(String key) {
        if (TextUtils.isEmpty(key))
            return null;
        return mLruCache.get(key);
    }

    /**
     * 将图片存储到LruCache
     */
    public void addTypefaceToLruCache(String key, Typeface typeface) {
        if (TextUtils.isEmpty(key) || typeface == null)
            return;
        if (getTypefaceFromLruCache(key) == null) {
            mLruCache.put(key, typeface);
        }
    }
}
