package com.kakaxicm.geekming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;

/**
 * Created by star on 15/12/30.
 */
@ContentViewAnnotation(value = R.layout.activity_viewgtoup_entry)
public class CustomViewGroupEntryActivity extends BaseActivity{
    @ViewIdAnnotation(value = R.id.vertical_sliding_title_entry)
    private View mVerticalSlidingTitleEntry;
    @ViewIdAnnotation(value = R.id.flow_layout_entry)
    private View mFlowLayoutEntry;
    @ViewIdAnnotation(value = R.id.bubble_imageview_entry)
    private View mShapeBitmapViewEntry;
    @ViewIdAnnotation(value = R.id.behavior_entry1)
    private View mBehaviorEntry1;
    @ViewIdAnnotation(value = R.id.behavior_entry2)
    private View mBehaviorEntry2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVerticalSlidingTitleEntry.setOnClickListener(this);
        mFlowLayoutEntry.setOnClickListener(this);
        mShapeBitmapViewEntry.setOnClickListener(this);
        mBehaviorEntry1.setOnClickListener(this);
        mBehaviorEntry2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.vertical_sliding_title_entry:
                intent.setClass(mContext, VerticalSlidingTitleActivity.class);
                startActivity(intent);
                break;
            case R.id.flow_layout_entry:
                intent.setClass(mContext, FlowLayoutActivity.class);
                startActivity(intent);
                break;
            case R.id.bubble_imageview_entry:
                intent.setClass(mContext, ShapeBitmapActivity.class);
                startActivity(intent);
                break;
            case R.id.behavior_entry1:
                intent.setClass(mContext, Behavior1Activity.class);
                startActivity(intent);
                break;
            case R.id.behavior_entry2:
                intent.setClass(mContext, Behavior2Activity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
