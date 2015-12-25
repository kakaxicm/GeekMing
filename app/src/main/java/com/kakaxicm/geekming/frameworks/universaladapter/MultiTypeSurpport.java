package com.kakaxicm.geekming.frameworks.universaladapter;

/**
 * Created by kakaxicm on 2015/11/4.
 */
public interface MultiTypeSurpport<T> {
    int getViewTypeCount();
    int getLayoutId(int position, T item);
    int getItemViewType(int postion, T item);
}
