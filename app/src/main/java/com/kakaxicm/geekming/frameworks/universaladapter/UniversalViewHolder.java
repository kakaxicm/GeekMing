package com.kakaxicm.geekming.frameworks.universaladapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kakaxicm on 2015/12/25.
 */
public class UniversalViewHolder {
    //layout id of item
    private int mLayoutId;
    //hold child view
    private SparseArray<View> mViews;
    //convertView
    private View mContentView;

    /**
     * when getView() is invoked and convertView=null, new Viewholder should be constructed
     */
    private UniversalViewHolder(Context context, ViewGroup parent, int layoutId) {
        mViews = new SparseArray<>();
        mContentView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mContentView.setTag(this);
        mLayoutId = layoutId;
    }


    /**
     * whether convertView is reused, return a viewholder
     */
    public static UniversalViewHolder buildViewHolder(Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new UniversalViewHolder(context, parent, layoutId);
        }
        UniversalViewHolder viewHolder = (UniversalViewHolder) convertView.getTag();
        //multi type surpport
        if (viewHolder.mLayoutId != layoutId) {
            return new UniversalViewHolder(context, parent, layoutId);
        }
        return viewHolder;
    }

    /**
     * get childView by id
     */
    public <T extends View> T getItemChildView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * get convertView
     */
    public View getConvertView() {
        return mContentView;
    }

    public int getmLayoutId() {
        return mLayoutId;
    }

    /***********
     * these methods provide some methods of bind view
     * TODO supplement
     **********/
    public void setTextView(int id, String text) {
        TextView tv = getItemChildView(id);
        tv.setText(text);
    }

    public void setImageView(int id, int resId) {
        ImageView img = getItemChildView(id);
        img.setImageResource(resId);
    }
}
