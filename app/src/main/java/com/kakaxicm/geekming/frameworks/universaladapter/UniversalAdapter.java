package com.kakaxicm.geekming.frameworks.universaladapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by kakaxicm on 2015/12/25.
 *
 */
public abstract class UniversalAdapter<T> extends BaseAdapter{
    protected List<T> mData;
    protected Context mContext;
    protected int mLayoutId;

    //MultiType Surpport
    MultiTypeSurpport<T> mMultiTypeSurpport;
    public UniversalAdapter(Context context, List<T> data, MultiTypeSurpport<T> multiTypeSurpport) {
        mData = data;
        mContext = context;
        mMultiTypeSurpport = multiTypeSurpport;
    }

    public UniversalAdapter(Context context, List<T> data, int layoutId) {
        mContext = context;
        mData = data;
        mLayoutId = layoutId;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiTypeSurpport != null) {
            return mMultiTypeSurpport.getViewTypeCount();
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiTypeSurpport != null) {
            return mMultiTypeSurpport.getItemViewType(position, mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // build universal viewholder to hold convertView and its childs
        UniversalViewHolder viewHolder = null;
        if(mMultiTypeSurpport == null) {
            viewHolder = UniversalViewHolder.buildViewHolder(mContext, convertView, parent, mLayoutId);
        } else {
            viewHolder = UniversalViewHolder.buildViewHolder(mContext, convertView, parent, mMultiTypeSurpport.getLayoutId(position, mData.get(position)));
        }

        bindView(viewHolder, position, mData.get(position));
        return viewHolder.getConvertView();
    }

    public abstract void bindView(UniversalViewHolder vh, int postion, T item);
}
