package com.kakaxicm.geekming;

import android.os.Bundle;
import android.widget.TextView;

import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.FlowLayout;
import com.kakaxicm.geekming.utils.StringUtils;

/**
 * Created by star on 16/1/3.
 */
@ContentViewAnnotation(value = R.layout.activity_flowlayout)
public class FlowLayoutActivity extends BaseActivity{
    @ViewIdAnnotation(value = R.id.fl)
    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int i = 0 ; i< 30; i++) {
            TextView tv = new TextView(this);
            tv.setText(StringUtils.getString("item" + i));
            mFlowLayout.addView(tv);
        }
    }
}
