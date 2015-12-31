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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVerticalSlidingTitleEntry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.vertical_sliding_title_entry:
                intent.setClass(mContext, VerticalSlidingTitleActivity.class);
                startActivity(intent);
                break;
        }
    }
}
