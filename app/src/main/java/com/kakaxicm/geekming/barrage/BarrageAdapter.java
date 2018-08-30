package com.kakaxicm.geekming.barrage;

import android.view.View;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by chenming on 2018/8/30
 */
public abstract class BarrageAdapter<M> {

    // 使用HashMap，以类型和对应view的栈为key-value存储，实现缓存
    private HashMap<Integer, Stack<View>> mCacheViews;
    private int[] mTypeArray;

    public BarrageAdapter() {
        mCacheViews = new HashMap<>();
        mTypeArray = getViewTypeArray();
        for (int i = 0; i < mTypeArray.length; i++) {
            Stack<View> stack = new Stack<>();
            mCacheViews.put(mTypeArray[i], stack);
        }
    }

    /**
     * 获取类型数组
     *
     * @return
     */
    public abstract int[] getViewTypeArray();

    /**
     * 获取单行弹幕 高度
     *
     * @return
     */
    public abstract int getSingleLineHeight();

    /**
     * 获取itemView
     *
     * @param entry
     * @param convertView
     * @return
     */
    public abstract View getView(M entry, View convertView);

    /**
     * 将弹幕itemView加入缓存（压栈）
     *
     * @param type
     * @param view
     */
    synchronized public void addViewToCache(int type, View view) {
        if (mCacheViews.containsKey(type)) {
            mCacheViews.get(type).push(view);
        } else {
            throw new Error("your cache has not this type");
        }
    }

    /**
     * 将itemView移出缓存（弹栈）
     *
     * @param type
     * @return
     */
    synchronized public View removeViewFromCache(int type) {
        if (mCacheViews.containsKey(type) && mCacheViews.get(type).size() > 0)
            return mCacheViews.get(type).pop();
        else
            return null;
    }

    /**
     * 减小缓存大小
     */
    synchronized public void shrinkCacheSize() {
        int[] typeArray = getViewTypeArray();
        for (int i = 0; i < typeArray.length; i++) {
            if (mCacheViews.containsKey(typeArray[i])) {
                Stack<View> typeStack = mCacheViews.get(typeArray[i]);
                int length = typeStack.size();
                // 循环弹栈，直到大小变为原来一半
                while (typeStack.size() > (int) (length / 2.0 + 0.5)) {
                    typeStack.pop();
                }
                mCacheViews.put(typeArray[i], typeStack);
            }
        }
    }

    /**
     * 清除缓存
     */
    synchronized public void clearCache(){
        if(mCacheViews.size() > 0){
            mCacheViews.clear();
        }
    }

    /**
     * 获取缓存大小
     *
     * @return
     */
    public int getCacheSize() {
        int size = 0;
        int[] types = getViewTypeArray();
        for (int i = 0; i < types.length; i++) {
            size = size + mCacheViews.get(types[i]).size();
        }
        return size;
    }
}
