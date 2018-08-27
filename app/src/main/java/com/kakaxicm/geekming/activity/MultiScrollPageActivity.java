package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

/**
 * Created by chenming on 2018/8/27
 */
public class MultiScrollPageActivity extends BaseActivity {
    SmartRefreshLayout refreshLayout;

    ImageView ivHeader;
    Toolbar toolbar;

    //ScrollView在RefreshLayout中滚动的偏移,用于设置图片的差分偏移
    private int mOffset;
    //当前的垂直滚动距离
    private int mScrollY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_scrollview);
        refreshLayout = findViewById(R.id.refreshLayout);
        toolbar = findViewById(R.id.toolbar);
        ivHeader = findViewById(R.id.iv_header);

        /**
         * 下拉和松手时候的背景图片和ToolBar处理
         */
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                //图片慢速偏移
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });

    }
}
