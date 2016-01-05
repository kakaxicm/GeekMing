package com.kakaxicm.geekming;

import android.os.Bundle;

import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.BitmapShapeView;

/**
 * Created by star on 16/1/5.
 */
@ContentViewAnnotation(value = R.layout.activity_shape_bitmap_view)
public class ShapeBitmapActivity extends BaseActivity{
    @ViewIdAnnotation(value = R.id.shape_view)
    private BitmapShapeView mBitmapShapeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmapShapeView.setImageSrc(R.drawable.category_mood);
    }
}
